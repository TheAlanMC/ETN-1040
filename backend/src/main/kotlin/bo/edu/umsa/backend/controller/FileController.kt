package bo.edu.umsa.backend.controller

import bo.edu.umsa.backend.dto.FileDto
import bo.edu.umsa.backend.dto.FilePartialDto
import bo.edu.umsa.backend.dto.ResponseDto
import bo.edu.umsa.backend.service.FileService
import bo.edu.umsa.backend.util.AuthUtil
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.*
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/v1/files")
class FileController @Autowired constructor(private val fileService: FileService) {
    companion object {
        private val logger = LoggerFactory.getLogger(FileController::class.java.name)
    }

    @GetMapping("/{fileId}")
    fun getFile(@PathVariable fileId: Long): ResponseEntity<ByteArray> {
        logger.info("Starting the API call to get the file")
        logger.info("GET /api/v1/files/$fileId")
        AuthUtil.verifyAuthTokenHasRoles(listOf("VER TAREAS", "CREAR TAREAS", "EDITAR TAREAS", "VER HERRAMIENTAS", "CREAR HERRAMIENTAS", "EDITAR HERRAMIENTAS").toTypedArray())
        val fileDto: FileDto = fileService.getFile(fileId)
        val headers = HttpHeaders()
        headers.contentType = MediaType.parseMediaType(fileDto.contentType)
        headers.contentDisposition = ContentDisposition.parse("inline; filename=${fileDto.filename}")
        logger.info("Success: File retrieved")
        return ResponseEntity(fileDto.fileData, headers, HttpStatus.OK)
    }

    @GetMapping("/{fileId}/thumbnail")
    fun getThumbnail(@PathVariable fileId: Long): ResponseEntity<ByteArray> {
        logger.info("Starting the API call to get the thumbnail")
        logger.info("GET /api/v1/files/$fileId/thumbnail")
        val fileDto: FileDto = fileService.getPicture(fileId.toInt())
        val headers = HttpHeaders()
        headers.contentType = MediaType.parseMediaType(fileDto.contentType)
        headers.contentDisposition = ContentDisposition.parse("inline; filename=${fileDto.filename}")
        logger.info("Success: Thumbnail retrieved")
        return ResponseEntity(fileDto.thumbnail, headers, HttpStatus.OK)
    }

    @PostMapping()
    fun uploadFile(@RequestParam("file") file: MultipartFile): ResponseEntity<ResponseDto<FilePartialDto>> {
        logger.info("Starting the API call to upload a file")
        logger.info("POST /api/v1/files")
        AuthUtil.verifyAuthTokenHasRoles(listOf("VER TAREAS", "CREAR TAREAS", "EDITAR TAREAS", "VER HERRAMIENTAS", "CREAR HERRAMIENTAS", "EDITAR HERRAMIENTAS").toTypedArray())
        val filePartialDto: FilePartialDto = fileService.uploadFile(file)
        logger.info("Success: File uploaded")
        return ResponseEntity(ResponseDto(true, "Archivo subido", filePartialDto), HttpStatus.CREATED)
    }
}