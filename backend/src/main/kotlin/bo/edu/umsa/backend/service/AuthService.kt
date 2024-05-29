package bo.edu.umsa.backend.service

import at.favre.lib.crypto.bcrypt.BCrypt
import bo.edu.umsa.backend.controller.AuthController
import bo.edu.umsa.backend.dto.AuthReqDto
import bo.edu.umsa.backend.dto.AuthResDto
import bo.edu.umsa.backend.dto.PasswordChangeDto
import bo.edu.umsa.backend.entity.AccountRecovery
import bo.edu.umsa.backend.entity.FirebaseToken
import bo.edu.umsa.backend.entity.User
import bo.edu.umsa.backend.exception.EtnException
import bo.edu.umsa.backend.repository.*
import bo.edu.umsa.backend.util.AuthUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class AuthService @Autowired constructor(
    private val firebaseTokenRepository: FirebaseTokenRepository,
    private val userRepository: UserRepository,
    private val groupRepository: GroupRepository,
    private val roleRepository: RoleRepository,
    private val emailService: EmailService,
    private val accountRecoveryRepository: AccountRecoveryRepository,
    private val firebaseMessagingService: FirebaseMessagingService
) {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(AuthController::class.java)
    }

    fun authenticate(credentials: AuthReqDto): AuthResDto {
        // Validate the fields
        if (credentials.email.isBlank() || credentials.password.isBlank()) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Empty fields", "Al menos un campo está vacío")
        }
        logger.info("User ${credentials.email} is trying to authenticate")
        // Verify if the user exists
        val userEntity: User = userRepository.findByEmailAndStatusIsTrue(credentials.email)
            ?: throw EtnException(HttpStatus.UNAUTHORIZED, "Error: User not found", "Usuario no encontrado")
        val currentPasswordInBCrypt = userEntity.password
        // Verify if the password is correct
        val verifyResult = BCrypt.verifyer().verify(credentials.password.toCharArray(), currentPasswordInBCrypt)
        if (!verifyResult.verified) {
            throw EtnException(HttpStatus.UNAUTHORIZED, "Error: Incorrect password", "Contraseña incorrecta")
        }
        // Save the Firebase token if it is not empty, and it is not already saved
        if (credentials.firebaseToken.isNotBlank()) {
            val firebaseTokenEntity = firebaseTokenRepository.findByFirebaseTokenAndStatusIsTrue(credentials.firebaseToken)
            if (firebaseTokenEntity == null) {
                val newFirebaseTokenEntity = FirebaseToken()
                newFirebaseTokenEntity.firebaseToken = credentials.firebaseToken
                newFirebaseTokenEntity.userId = userEntity.userId
                newFirebaseTokenEntity.isMobile = credentials.isMobile
                firebaseTokenRepository.save(newFirebaseTokenEntity)
            }
        }
        // Get the user roles
        val roleEntities = roleRepository.findAllByEmail(credentials.email)
        val roles = roleEntities.filter { role -> role.status }.distinctBy { role -> role.roleName }.map { role -> role.roleName }.toTypedArray()
        // Get the user groups
        val groupEntities = groupRepository.findAllByEmail(credentials.email)
        val groups = groupEntities.filter { group -> group.status }.distinctBy { group -> group.groupName }.map { group -> group.groupName }.toTypedArray()
        return AuthUtil.generateAuthAndRefreshToken(userEntity, roles, groups)
    }

    fun refreshToken(token: String): AuthResDto {
        // Validate the fields
        if (token.isBlank()) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Empty fields", "Al menos un campo está vacío")
        }
        AuthUtil.verifyIsRefreshToken(token)
        val email = AuthUtil.getSubjectFromAuthToken(token)
        logger.info("User $email is trying to refresh the token")
        // Verify if the user exists
        val userEntity: User = userRepository.findByEmailAndStatusIsTrue(email!!)
            ?: throw EtnException(HttpStatus.UNAUTHORIZED, "Error: User not found", "Usuario no encontrado")
        // Get the user roles
        val roleEntities = roleRepository.findAllByEmail(email)
        val roles = roleEntities.filter { role -> role.status }.distinctBy { role -> role.roleName }.map { role -> role.roleName }.toTypedArray()
        // Get the user groups
        val groupEntities = groupRepository.findAllByEmail(email)
        val groups = groupEntities.filter { group -> group.status }.distinctBy { group -> group.groupName }.map { group -> group.groupName }.toTypedArray()
        return AuthUtil.generateAuthAndRefreshToken(userEntity, roles, groups)
    }

    fun forgotPassword(passwordChangeDto: PasswordChangeDto) {
        val email = passwordChangeDto.email
        // Validate the fields
        if (email.isBlank()) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Empty fields", "Al menos un campo está vacío")
        }
        logger.info("User $email is trying to reset the password, sending an email with the code")
        // Verify if the user exists
        val userEntity: User = userRepository.findByEmailAndStatusIsTrue(email)
            ?: throw EtnException(HttpStatus.BAD_REQUEST, "Error: User not found", "Usuario no encontrado")
        // Check if user has an active account recovery
        val accountRecoveryEntities = accountRecoveryRepository.findAllByUserEmailAndStatusIsTrueAndStatusIsTrue(email)
        if (accountRecoveryEntities.isNotEmpty()) {
            // Disable all the active account recoveries
            accountRecoveryEntities.forEach { accountRecoveryEntity ->
                accountRecoveryEntity.status = false
                accountRecoveryRepository.save(accountRecoveryEntity)
            }
        }
        // Generate random number from 1000 to 9999
        val randomCode = (1000..9999).random()
        // Encrypt the random code
        val hashCode = BCrypt.withDefaults().hashToString(12, randomCode.toString().toCharArray())
        // Save the random code in the database
        val accountRecoveryEntity = AccountRecovery()
        accountRecoveryEntity.userId = userEntity.userId
        accountRecoveryEntity.hashCode = hashCode
        // Set the expiration date to 15 minutes
        accountRecoveryEntity.expirationDate = java.sql.Timestamp(System.currentTimeMillis() + 900000)
        accountRecoveryRepository.save(accountRecoveryEntity)
        // Send an email to the user with the random code
        emailService.sendEmail(email, "Reestablecer contraseña", "Su código de recuperación es: $randomCode, este código expirará en 15 minutos")
    }

    fun verification(passwordChangeDto: PasswordChangeDto) {
        val email = passwordChangeDto.email
        val code = passwordChangeDto.code
        // Validate the fields
        if (email.isBlank() || code.isBlank()) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Empty fields", "Al menos un campo está vacío")
        }
        logger.info("User is trying to verify the hash code")
        // Verify if the account recovery exists
        val accountRecoveryEntity = accountRecoveryRepository.findAllByUserEmailAndStatusIsTrueAndStatusIsTrue(email).firstOrNull()
            ?: throw EtnException(HttpStatus.UNAUTHORIZED, "Error: Account recovery not found", "Recuperación de cuenta no encontrada")
        // Verify if the hash code is correct
        val verifyResult = BCrypt.verifyer().verify(code.toCharArray(), accountRecoveryEntity.hashCode)
        if (!verifyResult.verified) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Hash code not valid", "Código no válido")
        }
        // Verify if the hash code is expired
        if (accountRecoveryEntity.expirationDate.before(java.sql.Timestamp(System.currentTimeMillis()))) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Hash code expired", "Código expirado")
        }
    }

    fun resetPassword(passwordChangeDto: PasswordChangeDto) {
        val email = passwordChangeDto.email
        val code = passwordChangeDto.code
        val password = passwordChangeDto.password
        val confirmPassword = passwordChangeDto.confirmPassword
        // Validate the fields
        if (email.isBlank() || code.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Empty fields", "Al menos un campo está vacío")
        }
        // Validate the password is at least 8 characters long
        if (password.length < 8) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Password must be at least 8 characters long", "La contraseña debe tener al menos 8 caracteres")
        }
        // Validate that the password and confirm password are the same
        if (password != confirmPassword) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Passwords do not match", "Las contraseñas no coinciden")
        }
        logger.info("User $email is trying to reset the password")
        // Verify if the user exists
        val userEntity: User = userRepository.findByEmailAndStatusIsTrue(email)
            ?: throw EtnException(HttpStatus.UNAUTHORIZED, "Error: User not found", "Usuario no encontrado")
        // Verify if the account recovery exists
        val accountRecoveryEntity = accountRecoveryRepository.findAllByUserEmailAndStatusIsTrueAndStatusIsTrue(email).firstOrNull()
            ?: throw EtnException(HttpStatus.UNAUTHORIZED, "Error: Account recovery not found", "Recuperación de cuenta no encontrada")
        // Verify if the hash code is correct
        val verifyResult = BCrypt.verifyer().verify(code.toCharArray(), accountRecoveryEntity.hashCode)
        if (!verifyResult.verified) {
            throw EtnException(HttpStatus.UNAUTHORIZED, "Error: Hash code not valid", "Código no válido")
        }
        // Verify if the hash code is expired
        if (accountRecoveryEntity.expirationDate.before(java.sql.Timestamp(System.currentTimeMillis()))) {
            throw EtnException(HttpStatus.UNAUTHORIZED, "Error: Hash code expired", "Código expirado")
        }
        accountRecoveryEntity.status = false
        accountRecoveryRepository.save(accountRecoveryEntity)
        // Encrypt the new password
        val newPassword = BCrypt.withDefaults().hashToString(12, password.toCharArray())
        // Update the user password
        userEntity.password = newPassword
        userRepository.save(userEntity)
    }
}