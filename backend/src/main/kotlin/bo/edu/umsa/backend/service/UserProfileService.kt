package bo.edu.umsa.backend.service

import at.favre.lib.crypto.bcrypt.BCrypt
import bo.edu.umsa.backend.dto.FileDto
import bo.edu.umsa.backend.dto.PasswordChangeDto
import bo.edu.umsa.backend.dto.ProfileDto
import bo.edu.umsa.backend.entity.User
import bo.edu.umsa.backend.exception.EtnException
import bo.edu.umsa.backend.mapper.ProfileMapper
import bo.edu.umsa.backend.repository.UserRepository
import bo.edu.umsa.backend.util.AuthUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class UserProfileService @Autowired constructor(private val userRepository: UserRepository, private val fileService: FileService) {
    companion object {
        private val logger = org.slf4j.LoggerFactory.getLogger(UserProfileService::class.java)
    }

    // Profile
    fun getProfile(): ProfileDto {
        val userId = AuthUtil.getUserIdFromAuthToken()
            ?: throw EtnException(HttpStatus.UNAUTHORIZED, "Error: Unauthorized", "No autorizado")
        logger.info("Getting the profile with id $userId")
        // Get the user
        val userEntity: User = userRepository.findByUserIdAndStatusIsTrue(userId)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: User not found", "Usuario no encontrado")
        return ProfileMapper.entityToDto(userEntity)
    }

    fun updateProfile(profileDto: ProfileDto) {
        // Validate that at least one field is being updated
        if (profileDto.firstName.isBlank() && profileDto.lastName.isBlank() && profileDto.phone.isBlank() && profileDto.description.isBlank()) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: At least one field must be updated", "Al menos un campo debe ser actualizado")
        }
        // Firstname and lastname cannot be blank
        if (profileDto.firstName.isBlank() || profileDto.lastName.isBlank()) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Firstname and lastname cannot be blank", "Nombre y apellido no pueden estar en blanco")
        }
        // Phone must be a number
        if (!profileDto.phone.isBlank() && !profileDto.phone.matches(Regex("\\d+"))) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Phone must be a number", "El teléfono debe ser un número")
        }
        // Validate that the profileId is the same as the user's id
        val userId = AuthUtil.getUserIdFromAuthToken()
            ?: throw EtnException(HttpStatus.UNAUTHORIZED, "Error: Unauthorized", "No autorizado")
        logger.info("Updating the profile with id $userId")
        // Get the user
        val userEntity: User = userRepository.findByUserIdAndStatusIsTrue(userId)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: User not found", "Usuario no encontrado")
        // Update the user
        userEntity.firstName = profileDto.firstName
        userEntity.lastName = profileDto.lastName
        userEntity.phone = profileDto.phone
        userEntity.description = profileDto.description
        userRepository.save(userEntity)
    }

    // Profile picture
    fun getProfilePicture(): FileDto {
        val email = AuthUtil.getEmailFromAuthToken()
            ?: throw EtnException(HttpStatus.UNAUTHORIZED, "Error: Unauthorized", "No autorizado")
        logger.info("Getting the profile picture of $email")
        // Get the user
        val userEntity: User = userRepository.findByEmailAndStatusIsTrue(email)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: User not found", "Usuario no encontrado")
        return fileService.getPicture(userEntity.filePhotoId)
    }

    fun uploadProfilePicture(file: MultipartFile) {
        val email = AuthUtil.getEmailFromAuthToken()
            ?: throw EtnException(HttpStatus.UNAUTHORIZED, "Error: Unauthorized", "No autorizado")
        logger.info("Uploading the profile picture of $email")
        // Get the user
        val userEntity: User = userRepository.findByEmailAndStatusIsTrue(email)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: User not found", "Usuario no encontrado")
        // Update the same file
        fileService.overwritePicture(file, userEntity.filePhotoId)
        // Create the thumbnail
        fileService.overwriteThumbnail(file, userEntity.filePhotoId)
    }

    fun updatePassword(passwordChangeDto: PasswordChangeDto) {
        // Validate the fields
        if (passwordChangeDto.oldPassword.isBlank() || passwordChangeDto.password.isBlank() || passwordChangeDto.confirmPassword.isBlank()) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Empty fields", "Al menos un campo está vacío")
        }
        // Validate that the new password is different from the old password
        if (passwordChangeDto.oldPassword == passwordChangeDto.password) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: The new password must be different from the old password", "La nueva contraseña debe ser diferente a la contraseña anterior")
        }
        // Validate that the new password is at least 8 characters long
        if (passwordChangeDto.password.length < 8) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: The new password must be at least 8 characters long", "La nueva contraseña debe tener al menos 8 caracteres")
        }
        // Validate that the password and confirm password are the same
        if (passwordChangeDto.password != passwordChangeDto.confirmPassword) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Passwords do not match", "Las contraseñas no coinciden")
        }
        val email = AuthUtil.getEmailFromAuthToken()
            ?: throw EtnException(HttpStatus.UNAUTHORIZED, "Error: Unauthorized", "No autorizado")
        logger.info("Updating the password of $email")
        // Get the user
        val userEntity: User = userRepository.findByEmailAndStatusIsTrue(email)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: User not found", "Usuario no encontrado")
        // Compare the old password
        if (!BCrypt.verifyer().verify(passwordChangeDto.oldPassword.toCharArray(), userEntity.password).verified) {
            throw EtnException(HttpStatus.UNAUTHORIZED, "Error: Old password is incorrect", "La contraseña anterior es incorrecta")
        }
        // Update the password
        userEntity.password = BCrypt.withDefaults().hashToString(12, passwordChangeDto.password.toCharArray())
        userRepository.save(userEntity)
    }
}
