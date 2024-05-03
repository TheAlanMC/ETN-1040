package bo.edu.umsa.backend.service

import bo.edu.umsa.backend.dto.FileDto
import bo.edu.umsa.backend.dto.UserDto
import bo.edu.umsa.backend.entity.User
import bo.edu.umsa.backend.exception.EtnException
import bo.edu.umsa.backend.mapper.UserMapper
import bo.edu.umsa.backend.repository.*
import bo.edu.umsa.backend.util.AuthUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class UserService @Autowired constructor(
    private val userRepository: UserRepository,
    private val fileService: FileService
){

    fun getProfilePicture(): FileDto {
        val username = AuthUtil.getUsernameFromAuthToken() ?: throw EtnException(HttpStatus.UNAUTHORIZED, "Error: Unauthorized","No autorizado")
        // Get the user
        val userEntity: User = userRepository.findByUsernameAndStatusIsTrue(username)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: User not found","Usuario no encontrado")
        return  fileService.getFile(userEntity.filePhotoId.toLong())
    }

    fun uploadProfilePicture(file: MultipartFile) {
        val username = AuthUtil.getUsernameFromAuthToken() ?: throw EtnException(HttpStatus.UNAUTHORIZED, "Error: Unauthorized","No autorizado")
        // Get the user
        val userEntity: User = userRepository.findByUsernameAndStatusIsTrue(username)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: User not found","Usuario no encontrado")
        // Upload the file
        val fileId = fileService.uploadFile(file)
        // Update the user
        userEntity.filePhotoId = fileId
        userRepository.save(userEntity)
    }

    fun getProfile():UserDto{
        val userId = AuthUtil.getUserIdFromAuthToken() ?: throw EtnException(HttpStatus.UNAUTHORIZED, "Error: Unauthorized","No autorizado")
        // Get the user
        val userEntity: User = userRepository.findByUserIdAndStatusIsTrue(userId)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: User not found","Usuario no encontrado")
        return UserMapper.entityToDto(userEntity)
    }

    fun updateProfile(userDto: UserDto) {
        // Validate that at least one field is being updated
        if (userDto.firstName.isBlank() && userDto.lastName.isBlank() && userDto.phone.isBlank() && userDto.description.isBlank()) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: At least one field must be updated","Al menos un campo debe ser actualizado")
        }
        // Firstname and lastname cannot be blank
        if (userDto.firstName.isBlank() || userDto.lastName.isBlank()) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Firstname and lastname cannot be blank","Nombre y apellido no pueden estar en blanco")
        }
        // Validate that the profileId is the same as the user's id
        val userId = AuthUtil.getUserIdFromAuthToken() ?: throw EtnException(HttpStatus.UNAUTHORIZED, "Error: Unauthorized","No autorizado")
        // Get the user
        val userEntity: User = userRepository.findByUserIdAndStatusIsTrue(userId)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: User not found","Usuario no encontrado")
        // Update the user
        userEntity.firstName = userDto.firstName
        userEntity.lastName = userDto.lastName
        userEntity.phone = userDto.phone
        userEntity.description = userDto.description
        userRepository.save(userEntity)
    }
}