package bo.edu.umsa.backend.controller

import bo.edu.umsa.backend.dto.NewProjectDto
import bo.edu.umsa.backend.dto.RoleDto
import bo.edu.umsa.backend.dto.ResponseDto
import bo.edu.umsa.backend.service.ProjectService
import bo.edu.umsa.backend.util.AuthUtil
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
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

    @PostMapping
    fun createProject(
        @RequestBody newProjectDto: NewProjectDto
    ): ResponseEntity<ResponseDto<Nothing>> {
        logger.info("Starting the API call to create the project")
        logger.info("POST /api/v1/projects")
        AuthUtil.verifyAuthTokenHasRole("CREAR PROYECTOS")
        projectService.createProject(newProjectDto)
        logger.info("Success: Project created")
        return ResponseEntity(ResponseDto(true,"El proyecto se ha creado", null), HttpStatus.CREATED)
    }
    
}