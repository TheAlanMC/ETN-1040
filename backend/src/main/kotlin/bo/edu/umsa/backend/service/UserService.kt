package bo.edu.umsa.backend.service

import bo.edu.umsa.backend.dto.FileDto
import bo.edu.umsa.backend.entity.File
import bo.edu.umsa.backend.entity.User
import bo.edu.umsa.backend.exception.EtnException
import bo.edu.umsa.backend.mapper.FileMapper
import bo.edu.umsa.backend.repository.*
import bo.edu.umsa.backend.util.AuthUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class UserService @Autowired constructor(
    private val userRepository: UserRepository,
    private val fileRepository: FileRepository
){

    // TODO: migrate to file service
    fun getProfilePicture(): FileDto {
        val username = AuthUtil.getUsernameFromAuthToken() ?: throw EtnException(HttpStatus.UNAUTHORIZED, "Error: Unauthorized","No autorizado")
        // Get the user
        val userEntity: User = userRepository.findByUsernameAndStatusIsTrue(username)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: User not found","Usuario no encontrado")

        // Get the file
        val fileEntity: File = fileRepository.findByFileIdAndStatusIsTrue(userEntity.filePhotoId.toLong())
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: File not found","Archivo no encontrado")
        return FileMapper.entityToDto(fileEntity)
    }

    fun uploadProfilePicture(file: MultipartFile) {
        val username = AuthUtil.getUsernameFromAuthToken() ?: throw EtnException(HttpStatus.UNAUTHORIZED, "Error: Unauthorized","No autorizado")
        // Get the user
        val userEntity: User = userRepository.findByUsernameAndStatusIsTrue(username)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: User not found","Usuario no encontrado")
        // Upload the file
        val fileEntity: File = File()
        fileEntity.filename = file.originalFilename!!
        fileEntity.contentType = file.contentType!!
        fileEntity.fileData = file.bytes
        // Save the file
        val savedFile: File = fileRepository.save(fileEntity)
        // Update the user
        userEntity.filePhotoId = savedFile.fileId
        userRepository.save(userEntity)
    }
}