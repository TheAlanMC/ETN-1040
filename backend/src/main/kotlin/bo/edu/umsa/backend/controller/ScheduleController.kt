package bo.edu.umsa.backend.controller

import bo.edu.umsa.backend.dto.ResponseDto
import bo.edu.umsa.backend.dto.ScheduleDto
import bo.edu.umsa.backend.service.ScheduleService
import bo.edu.umsa.backend.util.AuthUtil
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/schedules")
class ScheduleController @Autowired constructor(private val scheduleService: ScheduleService) {

    companion object {
        private val logger = LoggerFactory.getLogger(ScheduleController::class.java.name)
    }

    @GetMapping
    fun getSchedules(): ResponseEntity<ResponseDto<List<ScheduleDto>>> {
        logger.info("Starting the API call to get the schedules")
        logger.info("GET /api/v1/schedules")
        AuthUtil.verifyAuthTokenHasPermission("VER HORARIOS")
        val schedules: List<ScheduleDto> = scheduleService.getAllSchedules()
        logger.info("Success: Schedules retrieved")
        return ResponseEntity(ResponseDto(true, "Horarios recuperados", schedules), HttpStatus.OK)
    }
}