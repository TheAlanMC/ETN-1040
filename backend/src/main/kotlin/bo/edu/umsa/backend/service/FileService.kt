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
    private val fileRepository: FileRepository,
    private val thumbnailService: ThumbnailService
){
    companion object {
        private val logger = org.slf4j.LoggerFactory.getLogger(FileService::class.java)
    }

    fun getFile(fileId: Int): FileDto {
        logger.info("Getting the file with id $fileId")
        // Get the file
        val fileEntity: File = fileRepository.findByFileIdAndStatusIsTrue(fileId.toLong())
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: File not found","Archivo no encontrado")
        return FileMapper.entityToDto(fileEntity)
    }

    fun getThumbnail(fileId: Int): FileDto {
        logger.info("Getting the thumbnail with id $fileId")
        // Get the file
        val fileEntity: File = fileRepository.findByFileIdAndStatusIsTrue(fileId.toLong())
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: File not found","Archivo no encontrado")
        return FileMapper.entityToDto(fileEntity)
    }

    fun uploadFile(file: MultipartFile): Int {
        logger.info("Uploading the file ${file.originalFilename}")
        // Upload the file
        val fileEntity = File()
        fileEntity.filename = file.originalFilename!!
        fileEntity.contentType = file.contentType!!
        fileEntity.fileData = file.bytes
        // Save the file
        val savedFile: File = fileRepository.save(fileEntity)
        return savedFile.fileId
    }

    fun overwriteFile(file: MultipartFile, fileId: Int) {
        logger.info("Overwriting the file with id $fileId")
        // Overwrite the file
        val fileEntity: File = fileRepository.findByFileIdAndStatusIsTrue(fileId.toLong())
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: File not found","Archivo no encontrado")
        fileEntity.filename = file.originalFilename!!
        fileEntity.contentType = file.contentType!!
        fileEntity.fileData = file.bytes
        fileRepository.save(fileEntity)
    }

    fun overwriteThumbnail(thumbnail: MultipartFile, fileId: Int) {
        logger.info("Overwriting the thumbnail with id $fileId")
        // Generate the thumbnail
        val thumbnailBytes = thumbnailService.createThumbnail(thumbnail)
        // Overwrite the thumbnail
        val fileEntity: File = fileRepository.findByFileIdAndStatusIsTrue(fileId.toLong())
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: File not found","Archivo no encontrado")
        fileEntity.thumbnail = thumbnailBytes
        fileRepository.save(fileEntity)
    }

}