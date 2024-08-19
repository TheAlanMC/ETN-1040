package bo.edu.umsa.backend.controller

import bo.edu.umsa.backend.dto.*
import bo.edu.umsa.backend.service.PermissionService
import bo.edu.umsa.backend.service.SemesterService
import bo.edu.umsa.backend.util.AuthUtil
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/semesters")
class SemesterController @Autowired constructor(private val semesterService: SemesterService) {

    companion object {
        private val logger = LoggerFactory.getLogger(SemesterController::class.java.name)
    }

    @GetMapping
    fun getSemesters(): ResponseEntity<ResponseDto<List<SemesterDto>>> {
        logger.info("Starting the API call to get the semesters")
        logger.info("GET /api/v1/semesters")
        AuthUtil.verifyAuthTokenHasPermission("VER HORARIOS")
        val semesters: List<SemesterDto> = semesterService.getAllSemesters()
        logger.info("Success: Semesters retrieved")
        return ResponseEntity(ResponseDto(true, "Semestres recuperados", semesters), HttpStatus.OK)
    }

    @GetMapping("/{semesterId}")
    fun getSemesterById(@PathVariable semesterId: Long): ResponseEntity<ResponseDto<SemesterDto>> {
        logger.info("Starting the API call to get the semester with id $semesterId")
        logger.info("GET /api/v1/semesters/$semesterId")
        AuthUtil.verifyAuthTokenHasPermission("VER HORARIOS")
        val semester: SemesterDto = semesterService.getSemesterById(semesterId)
        logger.info("Success: Semester retrieved")
        return ResponseEntity(ResponseDto(true, "Semestre recuperado", semester), HttpStatus.OK)
    }

    @PostMapping
    fun createSemester(
        @RequestBody newSemesterDto: NewSemesterDto
    ): ResponseEntity<ResponseDto<String>> {
        logger.info("Starting the API call to create a new semester")
        logger.info("POST /api/v1/semesters")
        AuthUtil.verifyAuthTokenHasPermission("CREAR HORARIOS")
        semesterService.createSemester(newSemesterDto)
        logger.info("Success: Semester created")
        return ResponseEntity(ResponseDto(true,"Semestre creado",null), HttpStatus.CREATED)
    }

    @PutMapping("/{semesterId}")
    fun updateSemester(
        @PathVariable semesterId: Long,
        @RequestBody newSemesterDto: NewSemesterDto): ResponseEntity<ResponseDto<String>> {
        logger.info("Starting the API call to update the semester with id $semesterId")
        logger.info("PUT /api/v1/semesters/$semesterId")
        AuthUtil.verifyAuthTokenHasPermission("EDITAR HORARIOS")
        semesterService.updateSemester(semesterId, newSemesterDto)
        logger.info("Success: Semester updated")
        return ResponseEntity(ResponseDto(true,"Semestre actualizado",null), HttpStatus.OK)
    }

    @DeleteMapping("/{semesterId}")
    fun deleteSemester(@PathVariable semesterId: Long): ResponseEntity<ResponseDto<String>> {
        logger.info("Starting the API call to delete the semester with id $semesterId")
        logger.info("DELETE /api/v1/semesters/$semesterId")
        AuthUtil.verifyAuthTokenHasPermission("EDITAR HORARIOS")
        semesterService.deleteSemester(semesterId)
        logger.info("Success: Semester deleted")
        return ResponseEntity(ResponseDto(true,"Semestre eliminado",null), HttpStatus.OK)
    }

    @GetMapping("/{semesterId}/assistants")
    fun getAssistantsBySemesterId(@PathVariable semesterId: Long): ResponseEntity<ResponseDto<List<AssistantDto>>> {
        logger.info("Starting the API call to get the assistants by semester id")
        logger.info("GET /api/v1/semesters/{semesterId}/assistants")
        AuthUtil.verifyAuthTokenHasPermission("VER HORARIOS")
        val assistants: List<AssistantDto> = semesterService.getAssistantsBySemesterId(semesterId)
        logger.info("Success: Assistants retrieved")
        return ResponseEntity(ResponseDto(true, "Auxiliares recuperados", assistants), HttpStatus.OK)
    }

    @PostMapping("/{semesterId}/assistants")
    fun addAssistantsToSemester(
        @PathVariable semesterId: Long,
        @RequestBody assistantIds: Map<String, List<Long>>
    ): ResponseEntity<ResponseDto<String>> {
        logger.info("Starting the API call to add assistants to the semester with id $semesterId")
        logger.info("POST /api/v1/semesters/{semesterId}/assistants")
        AuthUtil.verifyAuthTokenHasPermission("EDITAR HORARIOS")
        semesterService.addAssistantsToSemester(semesterId, assistantIds["assistantIds"]!!)
        logger.info("Success: Assistants added")
        return ResponseEntity(ResponseDto(true, "Auxiliares agregados", null), HttpStatus.CREATED)
    }
}