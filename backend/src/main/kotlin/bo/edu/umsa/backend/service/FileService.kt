package bo.edu.umsa.backend.service

import bo.edu.umsa.backend.dto.FileDto
import bo.edu.umsa.backend.entity.File
import bo.edu.umsa.backend.exception.EtnException
import bo.edu.umsa.backend.mapper.FileMapper
import bo.edu.umsa.backend.repository.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class FileService @Autowired constructor(
    private val fileRepository: FileRepository
){

    fun getFile(fileId: Long): FileDto {
        // Get the file
        val fileEntity: File = fileRepository.findByFileIdAndStatusIsTrue(fileId)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: File not found","Archivo no encontrado")
        return FileMapper.entityToDto(fileEntity)
    }

    fun uploadFile(file: MultipartFile): Int {
        // Upload the file
        val fileEntity: File = File()
        fileEntity.filename = file.originalFilename!!
        fileEntity.contentType = file.contentType!!
        fileEntity.fileData = file.bytes
        // Save the file
        val savedFile: File = fileRepository.save(fileEntity)
        return savedFile.fileId
    }
}