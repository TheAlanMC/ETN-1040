package bo.edu.umsa.backend.controller

import bo.edu.umsa.backend.dto.ProjectDashboardDto
import bo.edu.umsa.backend.dto.ResponseDto
import bo.edu.umsa.backend.dto.TaskDashboardDto
import bo.edu.umsa.backend.service.DashboardService
import bo.edu.umsa.backend.util.AuthUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/dashboards")
class DashboardController @Autowired constructor(
    private val dashboardService: DashboardService
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(DashboardController::class.java)
    }

    @GetMapping("/tasks")
    fun getTaskDashboard(
        @RequestParam(defaultValue = "dateFrom") dateFrom: String,
        @RequestParam(defaultValue = "dateTo") dateTo: String,
    ): ResponseEntity<ResponseDto<TaskDashboardDto>> {
        logger.info("Starting the API call to get the task dashboard")
        logger.info("GET /dashboards/tasks")
        AuthUtil.verifyAuthTokenHasRole("VER DASHBOARD")
        val taskDashboard: TaskDashboardDto = dashboardService.getTaskDashboard(dateFrom, dateTo)
        logger.info("Success: Task dashboard retrieved")
        return ResponseEntity(ResponseDto(true, "Dashboard de tareas recuperado", taskDashboard), HttpStatus.OK)
    }

    @GetMapping("/projects")
    fun getProjectDashboard(
        @RequestParam(defaultValue = "dateFrom") dateFrom: String,
        @RequestParam(defaultValue = "dateTo") dateTo: String,
    ): ResponseEntity<ResponseDto<ProjectDashboardDto>> {
        logger.info("Starting the API call to get the project dashboard")
        logger.info("GET /dashboards/projects")
        AuthUtil.verifyAuthTokenHasRole("VER DASHBOARD")
        val projectDashboard: ProjectDashboardDto = dashboardService.getProjectDashboard(dateFrom, dateTo)
        logger.info("Success: Project dashboard retrieved")
        return ResponseEntity(ResponseDto(true, "Dashboard de proyectos recuperado", projectDashboard), HttpStatus.OK)
    }
}