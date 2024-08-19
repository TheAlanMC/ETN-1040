package bo.edu.umsa.backend.controller

import bo.edu.umsa.backend.dto.NewReplacedPartDto
import bo.edu.umsa.backend.dto.ReplacedPartDto
import bo.edu.umsa.backend.dto.ResponseDto
import bo.edu.umsa.backend.service.ReplacedPartService
import bo.edu.umsa.backend.util.AuthUtil
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/replaced-parts")
class ReplacedPartController @Autowired constructor(
    private val replacedPartService: ReplacedPartService,
) {

    companion object {
        private val logger = LoggerFactory.getLogger(ReplacedPartController::class.java.name)
    }

    @GetMapping("/{replacedPartId}")
    fun getReplacedPartById(@PathVariable replacedPartId: Long): ResponseEntity<ResponseDto<ReplacedPartDto>> {
        logger.info("Starting the API call to get the replaced part by id")
        logger.info("GET /api/v1/replaced-parts/$replacedPartId")
        AuthUtil.verifyAuthTokenHasPermission("VER TAREAS")
        val replacedPart: ReplacedPartDto = replacedPartService.getReplacedPartById(replacedPartId)
        logger.info("Success: Replaced part retrieved")
        return ResponseEntity(ResponseDto(true, "Reemplazo recuperado", replacedPart), HttpStatus.OK)
    }

    @PostMapping
    fun createReplacedPart(@RequestBody newReplacedPartDto: NewReplacedPartDto): ResponseEntity<ResponseDto<Nothing>> {
        logger.info("Starting the API call to create the replaced part")
        logger.info("POST /api/v1/replaced-parts")
        AuthUtil.verifyAuthTokenHasPermission("VER TAREAS")
        replacedPartService.createReplacedPart(newReplacedPartDto)
        logger.info("Success: Replaced part created")
        return ResponseEntity(ResponseDto(true, "El reemplazo se ha creado", null), HttpStatus.CREATED)
    }

    @PutMapping("/{replacedPartId}")
    fun updateReplacedPart(
        @PathVariable replacedPartId: Long,
        @RequestBody newReplacedPartDto: NewReplacedPartDto
    ): ResponseEntity<ResponseDto<Nothing>> {
        logger.info("Starting the API call to update the replaced part")
        logger.info("PUT /api/v1/replaced-parts/$replacedPartId")
        AuthUtil.verifyAuthTokenHasPermission("VER TAREAS")
        replacedPartService.updateReplacedPart(replacedPartId, newReplacedPartDto)
        logger.info("Success: Replaced part updated")
        return ResponseEntity(ResponseDto(true, "El reemplazo se ha actualizado", null), HttpStatus.OK)
    }

    @DeleteMapping("/{replacedPartId}")
    fun deleteReplacedPart(@PathVariable replacedPartId: Long): ResponseEntity<ResponseDto<Nothing>> {
        logger.info("Starting the API call to delete the replaced part")
        logger.info("DELETE /api/v1/replaced-parts/$replacedPartId")
        AuthUtil.verifyAuthTokenHasPermission("VER TAREAS")
        replacedPartService.deleteReplacedPart(replacedPartId)
        logger.info("Success: Replaced part deleted")
        return ResponseEntity(ResponseDto(true, "El reemplazo se ha eliminado", null), HttpStatus.OK)
    }
}