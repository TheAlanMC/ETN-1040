package bo.edu.umsa.backend.controller

import bo.edu.umsa.backend.dto.*
import bo.edu.umsa.backend.service.ReportService
import bo.edu.umsa.backend.util.AuthUtil
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/reports")
class ReportController @Autowired constructor(
    private val reportService: ReportService,
) {

    companion object {
        private val logger = LoggerFactory.getLogger(ReportController::class.java.name)
    }

    @GetMapping()
    fun getReports(
        @RequestParam(defaultValue = "reportId") sortBy: String,
        @RequestParam(defaultValue = "asc") sortType: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(required = false) dateFrom: String,
        @RequestParam(required = false) dateTo: String,
    ): ResponseEntity<ResponseDto<Page<ReportDto>>> {
        logger.info("Starting the API call to get the reports")
        logger.info("GET /api/v1/reports")
        AuthUtil.verifyAuthTokenHasRole("VER REPORTES GENERADOS")
        val reports: Page<ReportDto> = reportService.getReports(sortBy, sortType, page, size, dateFrom, dateTo)
        logger.info("Success: Reports retrieved")
        return ResponseEntity(ResponseDto(true, "Reportes recuperados", reports), HttpStatus.OK)
    }

    @PostMapping("/{reportType}")
    fun uploadReportFile(
        @RequestParam(defaultValue = "dateFrom") dateFrom: String,
        @RequestParam(defaultValue = "dateTo") dateTo: String,
        @PathVariable reportType: ReportType,
        @RequestBody file: FilePartialDto
    ): ResponseEntity<ResponseDto<Nothing>> {
        logger.info("Starting the API call to upload the report file")
        logger.info("POST /api/v1/reports")
        AuthUtil.verifyAuthTokenHasRoles(listOf("VER REPORTES DE TAREAS", "VER REPORTES DE PROYECTOS", "VER REPORTES EJECUTIVOS").toTypedArray())
        reportService.uploadReportFile(file, reportType, dateFrom, dateTo)
        logger.info("Success: Report file uploaded")
        return ResponseEntity(ResponseDto(true, "Reporte generado con éxito", null), HttpStatus.OK)
    }

    @GetMapping("/tasks/filters")
    fun getTaskFilters(
        @RequestParam(defaultValue = "dateFrom") dateFrom: String,
        @RequestParam(defaultValue = "dateTo") dateTo: String,
    ): ResponseEntity<ResponseDto<TaskReportFiltersDto>> {
        logger.info("Starting the API call to get the task filters")
        logger.info("GET /api/v1/reports/tasks/filters")
        AuthUtil.verifyAuthTokenHasRole("VER REPORTES DE TAREAS")
        val taskFilters: TaskReportFiltersDto = reportService.getTaskFilters(dateFrom, dateTo)
        logger.info("Success: Task filters retrieved")
        return ResponseEntity(ResponseDto(true, "Filtros de tareas recuperados", taskFilters), HttpStatus.OK)
    }

    @GetMapping("/tasks")
    fun getTaskReport(
        @RequestParam(defaultValue = "taskId") sortBy: String,
        @RequestParam(defaultValue = "asc") sortType: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(required = true) dateFrom: String,
        @RequestParam(required = true) dateTo: String,
        @RequestParam(required = false) projects: List<Int>?,
        @RequestParam(required = false) taskAssignees: List<Int>?,
        @RequestParam(required = false) statuses: List<String>?,
        @RequestParam(required = false) priorities: List<String>?,
    ): ResponseEntity<ResponseDto<Page<TaskReportDto>>> {
        logger.info("Starting the API call to get the tasks")
        logger.info("GET /api/v1/reports/tasks")
        AuthUtil.verifyAuthTokenHasRole("VER REPORTES DE TAREAS")
        val tasks: Page<TaskReportDto> = reportService.getProjectTasks(sortBy, sortType, page, size, dateFrom, dateTo, projects, taskAssignees, statuses, priorities)
        logger.info("Success: Tasks retrieved")
        return ResponseEntity(ResponseDto(true, "Tareas recuperadas", tasks), HttpStatus.OK)
    }

    @GetMapping("/projects/filters")
    fun getProjectFilters(
        @RequestParam(defaultValue = "dateFrom") dateFrom: String,
        @RequestParam(defaultValue = "dateTo") dateTo: String,
    ): ResponseEntity<ResponseDto<ProjectReportFiltersDto>> {
        logger.info("Starting the API call to get the project filters")
        logger.info("GET /api/v1/reports/projects/filters")
        AuthUtil.verifyAuthTokenHasRole("VER REPORTES DE PROYECTOS")
        val projectFilters: ProjectReportFiltersDto = reportService.getProjectFilters(dateFrom, dateTo)
        logger.info("Success: Project filters retrieved")
        return ResponseEntity(ResponseDto(true, "Filtros de proyectos recuperados", projectFilters), HttpStatus.OK)
    }

    @GetMapping("/projects")
    fun getProjectReport(
        @RequestParam(defaultValue = "projectId") sortBy: String,
        @RequestParam(defaultValue = "asc") sortType: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(required = true) dateFrom: String,
        @RequestParam(required = true) dateTo: String,
        @RequestParam(required = false) projectOwners: List<Int>?,
        @RequestParam(required = false) projectModerators: List<Int>?,
        @RequestParam(required = false) projectMembers: List<Int>?,
        @RequestParam(required = false) statuses: List<String>?,
    ): ResponseEntity<ResponseDto<Page<ProjectReportDto>>> {
        logger.info("Starting the API call to get the projects")
        logger.info("GET /api/v1/reports/projects")
        AuthUtil.verifyAuthTokenHasRole("VER REPORTES DE PROYECTOS")
        val projects: Page<ProjectReportDto> = reportService.getProjects(sortBy, sortType, page, size, dateFrom, dateTo, projectOwners, projectModerators, projectMembers, statuses)
        logger.info("Success: Projects retrieved")
        return ResponseEntity(ResponseDto(true, "Proyectos recuperados", projects), HttpStatus.OK)
    }

    @GetMapping("/executives")
    fun getExecutiveReport(
        @RequestParam(required = true) dateFrom: String,
        @RequestParam(required = true) dateTo: String,
    ): ResponseEntity<ResponseDto<ExecutiveReportDto>> {
        logger.info("Starting the API call to get the executive report")
        logger.info("GET /api/v1/reports/executive")
        AuthUtil.verifyAuthTokenHasRole("VER REPORTES EJECUTIVOS")
        val executiveReport: ExecutiveReportDto = reportService.getExecutiveReport(dateFrom, dateTo)
        logger.info("Success: Executive report retrieved")
        return ResponseEntity(ResponseDto(true, "Reporte ejecutivo recuperado", executiveReport), HttpStatus.OK)
    }
}