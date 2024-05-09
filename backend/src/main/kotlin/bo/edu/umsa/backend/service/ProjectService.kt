package bo.edu.umsa.backend.service

import bo.edu.umsa.backend.dto.NewProjectDto
import bo.edu.umsa.backend.entity.Project
import bo.edu.umsa.backend.entity.ProjectModerator
import bo.edu.umsa.backend.entity.ProjectOwner
import bo.edu.umsa.backend.entity.ProjectMember
import bo.edu.umsa.backend.exception.EtnException
import bo.edu.umsa.backend.repository.*
import bo.edu.umsa.backend.util.AuthUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

@Service
class ProjectService @Autowired constructor(
    private val userRepository: UserRepository,
    private val projectRepository: ProjectRepository,
    private val projectOwnerRepository: ProjectOwnerRepository,
    private val projectModeratorRepository: ProjectModeratorRepository,
    private val projectMemberRepository: ProjectMemberRepository,
) {
    companion object {
        private val logger = org.slf4j.LoggerFactory.getLogger(ProjectService::class.java)
    }

    fun createProject(newProjectDto: NewProjectDto) {
        // Validate the name is not empty
        if (newProjectDto.projectName.isEmpty() || newProjectDto.dateFrom.isEmpty() || newProjectDto.dateTo.isEmpty()) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: At least one required field is blank","Al menos un campo requerido está en blanco")
        }
        // Validate the dates have the correct format
        try {
            val dateFrom = Timestamp.from(Instant.parse(newProjectDto.dateFrom))
            val dateTo = Timestamp.from(Instant.parse(newProjectDto.dateTo))
            logger.info("Date from: $dateFrom, Date to: $dateTo")
        } catch (e: Exception) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Date format is incorrect","El formato de fecha es incorrecto")
        }
        if (Timestamp.from(Instant.parse(newProjectDto.dateFrom)).after(Timestamp.from(Instant.parse(newProjectDto.dateTo)))) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Date range is incorrect","El rango de fechas es incorrecto")
        }
        // Validate the project moderators are not empty and valid
        if (newProjectDto.projectModeratorIds.isEmpty()) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: At least one moderator is required","Se requiere al menos un moderador")
        }
        // Validate the project members are not empty
        if (newProjectDto.projectMemberIds.isEmpty()) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: At least one member is required","Se requiere al menos un miembro del equipo")
        }
        // Validate the project moderators exist
        if (userRepository.findAllInUserIdAndStatusIsTrue(newProjectDto.projectModeratorIds.map { it.toLong() }).size != newProjectDto.projectModeratorIds.size) {
            throw EtnException(
                HttpStatus.BAD_REQUEST, "Error: At least one moderator is invalid", "Al menos un moderador es inválido"
            )
        }
        // Validate the project members exist
        if (userRepository.findAllInUserIdAndStatusIsTrue(newProjectDto.projectMemberIds.map { it.toLong() }).size != newProjectDto.projectMemberIds.size) {
            throw EtnException(
                HttpStatus.BAD_REQUEST, "Error: At least one member is invalid", "Al menos un miembro es inválido"
            )
        }
        // Get the project owner id from the token
        val userId = AuthUtil.getUserIdFromAuthToken() ?: throw EtnException(HttpStatus.UNAUTHORIZED, "Error: Unauthorized","No autorizado")
        logger.info("Creating a new project with owner id $userId")

        // Create the project
        val projectEntity = Project()
        projectEntity.projectName = newProjectDto.projectName
        projectEntity.projectDescription = newProjectDto.projectDescription
        projectEntity.dateFrom = Timestamp.from(Instant.parse(newProjectDto.dateFrom))
        projectEntity.dateTo = Timestamp.from(Instant.parse(newProjectDto.dateTo))
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
            val projectMemberEntity = ProjectMember()
            projectMemberEntity.projectId = projectEntity.projectId
            projectMemberEntity.userId = memberId
            projectMemberRepository.save(projectMemberEntity)
        }
        logger.info("Project members created for project ${projectEntity.projectId}")
    }
}