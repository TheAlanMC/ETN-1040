package bo.edu.umsa.backend.service

import bo.edu.umsa.backend.dto.NewProjectDto
import bo.edu.umsa.backend.dto.RoleDto
import bo.edu.umsa.backend.entity.Project
import bo.edu.umsa.backend.entity.ProjectModerator
import bo.edu.umsa.backend.entity.ProjectOwner
import bo.edu.umsa.backend.entity.ProjectTeam
import bo.edu.umsa.backend.exception.EtnException
import bo.edu.umsa.backend.mapper.RoleMapper
import bo.edu.umsa.backend.repository.ProjectModeratorRepository
import bo.edu.umsa.backend.repository.ProjectOwnerRepository
import bo.edu.umsa.backend.repository.ProjectRepository
import bo.edu.umsa.backend.repository.ProjectTeamRepository
import bo.edu.umsa.backend.util.AuthUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class ProjectService @Autowired constructor(
    private val projectRepository: ProjectRepository,
    private val projectOwnerRepository: ProjectOwnerRepository,
    private val projectModeratorRepository: ProjectModeratorRepository,
    private val projectTeamRepository: ProjectTeamRepository,
) {
    companion object {
        private val logger = org.slf4j.LoggerFactory.getLogger(ProjectService::class.java)
    }

    fun createProject(newProjectDto: NewProjectDto) {
        // Validate the name is not empty
        if (newProjectDto.projectName.isEmpty()) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: At least one required field is blank","Al menos un campo requerido está en blanco")
        }
        // Validate the dates are in the correct order
        if (newProjectDto.dateFrom > newProjectDto.dateTo) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Date range is incorrect","El rango de fechas es incorrecto")
        }
        // Validate the project moderators are not empty
        if (newProjectDto.projectModeratorIds.isEmpty()) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: At least one moderator is required","Se requiere al menos un moderador")
        }
        // Validate the project members are not empty
        if (newProjectDto.projectMemberIds.isEmpty()) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: At least one member is required","Se requiere al menos un miembro")
        }
        // Get the project owner id from the token
        val userId = AuthUtil.getUserIdFromAuthToken() ?: throw EtnException(HttpStatus.UNAUTHORIZED, "Error: Unauthorized","No autorizado")
        logger.info("Creating a new project with owner id $userId")
        // Create the project
        val projectEntity = Project()
        projectEntity.projectName = newProjectDto.projectName
        projectEntity.projectDescription = newProjectDto.projectDescription
        projectEntity.dateFrom = newProjectDto.dateFrom
        projectEntity.dateTo = newProjectDto.dateTo
        projectRepository.save(projectEntity)
        logger.info("Project created with id ${projectEntity.projectId}")

        // Create the project owner
        val projectOwnerEntity = ProjectOwner()
        projectOwnerEntity.projectId = projectEntity.projectId
        projectOwnerEntity.userId = userId.toInt()
        projectOwnerRepository.save(projectOwnerEntity)
        // Create the project moderators
        newProjectDto.projectModeratorIds.forEach { moderatorId ->
            val projectModeratorEntity = ProjectModerator()
            projectModeratorEntity.projectId = projectEntity.projectId
            projectModeratorEntity.userId = moderatorId
            projectModeratorRepository.save(projectModeratorEntity)
        }
        logger.info("Project moderators created for project ${projectEntity.projectId}")

        // Create the project members
        newProjectDto.projectMemberIds.forEach { memberId ->
            val projectTeamEntity = ProjectTeam()
            projectTeamEntity.projectId = projectEntity.projectId
            projectTeamEntity.userId = memberId
            projectTeamRepository.save(projectTeamEntity)
        }
        logger.info("Project members created for project ${projectEntity.projectId}")
    }
}