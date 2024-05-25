package bo.edu.umsa.backend.controller

import bo.edu.umsa.backend.dto.*
import bo.edu.umsa.backend.service.ProjectService
import bo.edu.umsa.backend.util.AuthUtil
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/projects")
class ProjectController @Autowired constructor(
    private val projectService: ProjectService,
) {

    companion object {
        private val logger = LoggerFactory.getLogger(ProjectController::class.java.name)
    }

    @GetMapping("/all")
    fun getAllProjects(): ResponseEntity<ResponseDto<List<ProjectPartialDto>>> {
        logger.info("Starting the API call to get all projects")
        logger.info("GET /api/v1/projects/all")
        AuthUtil.verifyAuthTokenHasRoles(listOf("VER TAREAS", "CREAR TAREAS", "EDITAR TAREAS").toTypedArray())
        val projects: List<ProjectPartialDto> = projectService.getAllProjects()
        logger.info("Success: All projects retrieved")
        return ResponseEntity(ResponseDto(true, "Proyectos recuperados", projects), HttpStatus.OK)
    }

    @GetMapping
    fun getProjects(
        @RequestParam(defaultValue = "userId") sortBy: String,
        @RequestParam(defaultValue = "asc") sortType: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(required = false) keyword: String?,
        ): ResponseEntity<ResponseDto<Page<ProjectPartialDto>>> {
        logger.info("Starting the API call to get the projects")
        logger.info("GET /api/v1/projects")
        AuthUtil.verifyAuthTokenHasRole("VER PROYECTOS")
        val projects: Page<ProjectPartialDto> = projectService.getProjects(sortBy, sortType, page, size, keyword)
        logger.info("Success: Projects retrieved")
        return ResponseEntity(ResponseDto(true, "Proyectos recuperados", projects), HttpStatus.OK)
    }

    @GetMapping("/{projectId}")
    fun getProjectById(
        @PathVariable projectId: Long,
    ): ResponseEntity<ResponseDto<ProjectDto>> {
        logger.info("Starting the API call to get the project by id")
        logger.info("GET /api/v1/projects/$projectId")
        AuthUtil.verifyAuthTokenHasRole("VER PROYECTOS")
        val project: ProjectDto = projectService.getProjectById(projectId)
        logger.info("Success: Project retrieved")
        return ResponseEntity(ResponseDto(true, "Proyecto recuperado", project), HttpStatus.OK)
    }

    @PostMapping
    fun createProject(@RequestBody newProjectDto: NewProjectDto): ResponseEntity<ResponseDto<Nothing>> {
        logger.info("Starting the API call to create the project")
        logger.info("POST /api/v1/projects")
        AuthUtil.verifyAuthTokenHasRole("CREAR PROYECTOS")
        projectService.createProject(newProjectDto)
        logger.info("Success: Project created")
        return ResponseEntity(ResponseDto(true, "El proyecto se ha creado", null), HttpStatus.CREATED)
    }

    @PutMapping("/{projectId}")
    fun updateProject(
        @PathVariable projectId: Long,
        @RequestBody projectDto: NewProjectDto,
    ): ResponseEntity<ResponseDto<Nothing>> {
        logger.info("Starting the API call to update the project")
        logger.info("PUT /api/v1/projects/$projectId")
        AuthUtil.verifyAuthTokenHasRole("EDITAR PROYECTOS")
        projectService.updateProject(projectId, projectDto)
        logger.info("Success: Project updated")
        return ResponseEntity(ResponseDto(true, "El proyecto se ha actualizado", null), HttpStatus.OK)
    }

    @DeleteMapping("/{projectId}")
    fun deleteProject(
        @PathVariable projectId: Long,
    ): ResponseEntity<ResponseDto<Nothing>> {
        logger.info("Starting the API call to delete the project")
        logger.info("DELETE /api/v1/projects/$projectId")
        AuthUtil.verifyAuthTokenHasRole("EDITAR PROYECTOS")
        projectService.deleteProject(projectId)
        logger.info("Success: Project deleted")
        return ResponseEntity(ResponseDto(true, "El proyecto se ha eliminado", null), HttpStatus.OK)
    }

    @PutMapping("/{projectId}/close")
    fun closeProject(
        @PathVariable projectId: Long,
        @RequestBody closeProjectDto: CloseProjectDto,
    ): ResponseEntity<ResponseDto<Nothing>> {
        logger.info("Starting the API call to close the project")
        logger.info("PUT /api/v1/projects/$projectId/close")
        AuthUtil.verifyAuthTokenHasRole("EDITAR PROYECTOS")
        projectService.closeProject(projectId, closeProjectDto)
        logger.info("Success: Project closed")
        return ResponseEntity(ResponseDto(true, "El proyecto se ha cerrado", null), HttpStatus.OK)
    }

    @GetMapping("/{projectId}/tasks")
    fun getProjectTasks(
        @PathVariable projectId: Long,
        @RequestParam(defaultValue = "taskId") sortBy: String,
        @RequestParam(defaultValue = "asc") sortType: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(required = false) keyword: String?,
        @RequestParam(required = false) statuses: List<String>?,
        @RequestParam(required = false) priorities: List<String>?,
        @RequestParam(required = false) dateFrom: String?,
        @RequestParam(required = false) dateTo: String?,
    ): ResponseEntity<ResponseDto<Page<TaskPartialDto>>> {
        logger.info("Starting the API call to get the tasks")
        logger.info("GET /api/v1/projects/$projectId/tasks")
        AuthUtil.verifyAuthTokenHasRole("VER TAREAS")
        val tasks: Page<TaskPartialDto> = projectService.getProjectTasks(projectId, sortBy, sortType, page, size, keyword, statuses, priorities, dateFrom, dateTo)
        logger.info("Success: Tasks retrieved")
        return ResponseEntity(ResponseDto(true, "Tareas recuperadas", tasks), HttpStatus.OK)
    }
}