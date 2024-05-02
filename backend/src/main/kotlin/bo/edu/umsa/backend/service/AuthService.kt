package bo.edu.umsa.backend.service

import at.favre.lib.crypto.bcrypt.BCrypt
import bo.edu.umsa.backend.controller.AuthController
import bo.edu.umsa.backend.dto.AuthReqDto
import bo.edu.umsa.backend.dto.AuthResDto
import bo.edu.umsa.backend.entity.User
import bo.edu.umsa.backend.exception.EtnException
import bo.edu.umsa.backend.repository.GroupRepository
import bo.edu.umsa.backend.repository.RoleRepository
import bo.edu.umsa.backend.repository.UserRepository
import bo.edu.umsa.backend.util.AuthUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class AuthService @Autowired constructor(
    private val userRepository: UserRepository,
    private val groupRepository: GroupRepository,
    private val roleRepository: RoleRepository
){
    companion object {
        val logger: Logger = LoggerFactory.getLogger(AuthController::class.java)
    }

    fun authenticate(credentials: AuthReqDto): AuthResDto {
        logger.info("User ${credentials.email} is trying to authenticate")
        // Verify if the user exists
        val userEntity: User = userRepository.findByUsernameAndStatusIsTrue(credentials.email)
            ?: throw EtnException(HttpStatus.UNAUTHORIZED, "Error: User not found","Usuario no encontrado")
        val currentPasswordInBCrypt = userEntity.password

        // Verify if the password is correct
        val verifyResult = BCrypt.verifyer().verify(
            credentials.password.toCharArray(),
            currentPasswordInBCrypt
        )
        if (!verifyResult.verified) {
            throw EtnException(HttpStatus.UNAUTHORIZED, "Error: Incorrect password","Contraseña incorrecta")
        }

        // Get the user roles
        val roleEntities = roleRepository.findAllByUsername(credentials.email)
        val roles = roleEntities.map { role -> role.roleName }.toTypedArray()

        // Get the user groups
        val groupEntities = groupRepository.findAllByUsername(credentials.email)
        val groups = groupEntities.map { group -> group.groupName }.toTypedArray()

        return AuthUtil.generateAuthAndRefreshToken(userEntity, roles, groups)
    }

    fun refreshToken(token: String): AuthResDto {
        AuthUtil.verifyIsRefreshToken(token)
        val username = AuthUtil.getUsernameFromAuthToken(token)
        logger.info("User $username is trying to refresh the token")

        // Verify if the user exists
        val userEntity: User = userRepository.findByUsernameAndStatusIsTrue(username!!)
            ?: throw EtnException(HttpStatus.UNAUTHORIZED, "Error: User not found","Usuario no encontrado")

        // Get the user roles
        val roleEntities = roleRepository.findAllByUsername(username)
        val roles = roleEntities.map { role -> role.roleName }.toTypedArray()

        // Get the user groups
        val groupEntities = groupRepository.findAllByUsername(username)
        val groups = groupEntities.map { group -> group.groupName }.toTypedArray()

        return AuthUtil.generateAuthAndRefreshToken(userEntity, roles, groups)
    }
}