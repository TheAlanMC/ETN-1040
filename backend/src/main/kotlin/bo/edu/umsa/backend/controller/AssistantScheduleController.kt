package bo.edu.umsa.backend.controller

import bo.edu.umsa.backend.dto.AssistantScheduleDto
import bo.edu.umsa.backend.dto.CurrentAssistantScheduleDto
import bo.edu.umsa.backend.dto.NewAssistantScheduleDto
import bo.edu.umsa.backend.dto.ResponseDto
import bo.edu.umsa.backend.service.AssistantScheduleService
import bo.edu.umsa.backend.util.AuthUtil
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/v1/assistant-schedules")
class  AssistantScheduleController @Autowired constructor(private val assistantScheduleService: AssistantScheduleService) {

    companion object {
        private val logger = LoggerFactory.getLogger(AssistantScheduleController::class.java.name)
    }

    @GetMapping("/semesters/{semesterId}")
    fun getAssistantScheduleBySemesterId(@PathVariable semesterId: Long): ResponseEntity<ResponseDto<List<AssistantScheduleDto>>> {
        logger.info("Starting the API call to get the assistant schedules for the semester with id $semesterId")
        logger.info("GET /api/v1/assistant-schedules/semester/$semesterId")
        AuthUtil.verifyAuthTokenHasPermission("VER HORARIOS")
        val assistantSchedules: List<AssistantScheduleDto> = assistantScheduleService.getAssistantScheduleBySemesterId(semesterId)
        logger.info("Success: Assistant schedules retrieved by semester")
        return ResponseEntity(ResponseDto(true, "Horarios de auxiliares recuperados", assistantSchedules), HttpStatus.OK)
    }

    @GetMapping("/assistants/{assistantId}")
    fun getAssistantScheduleByAssistantId(@PathVariable assistantId: Long): ResponseEntity<ResponseDto<AssistantScheduleDto>> {
        logger.info("Starting the API call to get the assistant schedules for the assistant with id $assistantId")
        logger.info("GET /api/v1/assistant-schedules/assistant/$assistantId")
        AuthUtil.verifyAuthTokenHasPermission("VER HORARIOS")
        val assistantSchedule: AssistantScheduleDto = assistantScheduleService.getAssistantScheduleByAssistantId(assistantId)
        logger.info("Success: Assistant schedules retrieved by assistant")
        return ResponseEntity(ResponseDto(true, "Horarios de auxiliares recuperados", assistantSchedule), HttpStatus.OK)
    }

    @PostMapping("/semesters/{semesterId}/custom")
    fun addCustomAssistantsScheduleToSemester(
        @PathVariable semesterId: Long,
        @RequestBody newAssistantScheduleDto: List<NewAssistantScheduleDto>
    ): ResponseEntity<ResponseDto<Nothing>> {
        logger.info("Starting the API call to add the assistant schedules to the semester with id $semesterId")
        logger.info("POST /api/v1/assistant-schedules/semester/$semesterId/custom")
        AuthUtil.verifyAuthTokenHasPermissions(listOf("CREAR HORARIOS", "EDITAR HORARIOS").toTypedArray())
        assistantScheduleService.addCustomAssistantsScheduleToSemester(semesterId, newAssistantScheduleDto)
        logger.info("Success: Assistant schedules added to semester")
        return ResponseEntity(ResponseDto(true, "Horarios de auxiliares añadidos al semestre", null), HttpStatus.OK)
    }

    @PostMapping("/semesters/{semesterId}/random")
    fun addRandomAssistantsScheduleToSemester(@PathVariable semesterId: Long): ResponseEntity<ResponseDto<Nothing>> {
        logger.info("Starting the API call to add random assistant schedules to the semester with id $semesterId")
        logger.info("POST /api/v1/assistant-schedules/semester/$semesterId/random")
        AuthUtil.verifyAuthTokenHasPermissions(listOf("CREAR HORARIOS", "EDITAR HORARIOS").toTypedArray())
        assistantScheduleService.addRandomAssistantsScheduleToSemester(semesterId)
        logger.info("Success: Random assistant schedules added to semester")
        return ResponseEntity(ResponseDto(true, "Horarios de auxiliares aleatorios añadidos al semestre", null), HttpStatus.OK)
    }

    @GetMapping("/current")
    fun getLastSemesterAssistantSchedules(): ResponseEntity<ResponseDto<List<CurrentAssistantScheduleDto>>> {
        logger.info("Starting the API call to get the assistant schedules for today")
        logger.info("GET /api/v1/assistant-schedules/today")
        AuthUtil.verifyAuthTokenHasPermission("VER DASHBOARD")
        val assistantSchedules: List<CurrentAssistantScheduleDto> = assistantScheduleService.getAssistantSchedulesForToday()
        logger.info("Success: Assistant schedules retrieved for today")
        return ResponseEntity(ResponseDto(true, "Horarios de auxiliares recuperados para hoy", assistantSchedules), HttpStatus.OK)
    }
}