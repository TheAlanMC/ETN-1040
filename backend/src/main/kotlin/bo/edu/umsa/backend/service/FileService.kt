package bo.edu.umsa.backend.service

import bo.edu.umsa.backend.dto.FileDto
import bo.edu.umsa.backend.dto.FilePartialDto
import bo.edu.umsa.backend.entity.File
import bo.edu.umsa.backend.exception.EtnException
import bo.edu.umsa.backend.mapper.FileMapper
import bo.edu.umsa.backend.mapper.FilePartialMapper
import bo.edu.umsa.backend.repository.FileRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class FileService @Autowired constructor(private val fileRepository: FileRepository, private val thumbnailService: ThumbnailService) {
    companion object {
        private val logger = org.slf4j.LoggerFactory.getLogger(FileService::class.java)
    }

    fun getFile(fileId: Long): FileDto {
        logger.info("Getting the file with id $fileId")
        // Get the file
        val fileEntity: File = fileRepository.findByFileIdAndStatusIsTrue(fileId)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: File not found", "Archivo no encontrado")
        return FileMapper.entityToDto(fileEntity)
    }

    fun uploadFile(file: MultipartFile): FilePartialDto {
        logger.info("Uploading the file ${file.originalFilename}")
        val fileEntity = File()
        fileEntity.filename = sanitizeFilename(file.originalFilename!!)
        fileEntity.contentType = file.contentType!!
        fileEntity.fileData = file.bytes
        fileEntity.fileSize = file.size.toInt()
        try {
            if (file.contentType!!.startsWith("image")) {
                val thumbnailBytes = thumbnailService.createThumbnail(file, 100)
                fileEntity.thumbnail = thumbnailBytes
                fileEntity.isPicture = true
            }
        } catch (e: Exception) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: File is not an image", "La imagen con extensión ${file.originalFilename?.substringAfterLast(".")} no es válida")
        }

        // Save the file
        val savedFile: File = fileRepository.save(fileEntity)
        return FilePartialMapper.entityToDto(savedFile)
    }

    fun getPicture(fileId: Int): FileDto {
        logger.info("Getting the picture with id $fileId")
        // Get the file
        val fileEntity: File = fileRepository.findByFileIdAndStatusIsTrue(fileId.toLong())
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: File not found", "Archivo no encontrado")
        if (!fileEntity.isPicture) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: File is not a picture", "El archivo no es una imagen")
        }
        return FileMapper.entityToDto(fileEntity)
    }

    fun overwritePicture(file: MultipartFile, fileId: Int) {
        logger.info("Overwriting the picture with id $fileId")
        // Overwrite the file
        val fileEntity: File = fileRepository.findByFileIdAndStatusIsTrue(fileId.toLong())
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: File not found", "Archivo no encontrado")
        fileEntity.filename = sanitizeFilename(file.originalFilename!!)
        fileEntity.contentType = file.contentType!!
        fileEntity.fileData = file.bytes
        fileEntity.fileSize = file.size.toInt()
        fileRepository.save(fileEntity)
    }

    fun overwriteThumbnail(thumbnail: MultipartFile, fileId: Int) {
        logger.info("Overwriting the thumbnail with id $fileId")
        // Generate the thumbnail
        val thumbnailBytes = thumbnailService.createThumbnail(thumbnail,50)
        // Overwrite the thumbnail
        val fileEntity: File = fileRepository.findByFileIdAndStatusIsTrue(fileId.toLong())
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: File not found", "Archivo no encontrado")
        fileEntity.thumbnail = thumbnailBytes
        fileRepository.save(fileEntity)
    }

    fun sanitizeFilename(filename: String): String {
        return filename.map { if (it.code in 0..255) it else '_' }.joinToString("")
    }

}