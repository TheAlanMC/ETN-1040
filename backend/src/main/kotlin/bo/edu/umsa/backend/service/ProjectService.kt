package bo.edu.umsa.backend.service

import bo.edu.umsa.backend.dto.*
import bo.edu.umsa.backend.entity.*
import bo.edu.umsa.backend.exception.EtnException
import bo.edu.umsa.backend.mapper.ProjectMapper
import bo.edu.umsa.backend.mapper.ProjectPartialMapper
import bo.edu.umsa.backend.mapper.TaskMapper
import bo.edu.umsa.backend.repository.*
import bo.edu.umsa.backend.specification.ProjectSpecification
import bo.edu.umsa.backend.specification.TaskSpecification
import bo.edu.umsa.backend.util.AuthUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.time.Instant

@Service
class ProjectService @Autowired constructor(
    private val userRepository: UserRepository,
    private val projectRepository: ProjectRepository,
    private val projectOwnerRepository: ProjectOwnerRepository,
    private val projectModeratorRepository: ProjectModeratorRepository,
    private val projectMemberRepository: ProjectMemberRepository,
    private val taskRepository: TaskRepository,
    private val taskAssigneeRepository: TaskAssigneeRepository,
) {
    companion object {
        private val logger = org.slf4j.LoggerFactory.getLogger(ProjectService::class.java)
    }

    fun getAllProjects(): List<ProjectPartialDto> {
        logger.info("Getting all users")
        val projectEntities = projectRepository.findAllByStatusIsTrueOrderByProjectIdAsc()
        return projectEntities.map { ProjectPartialMapper.entityToDto(it) }
    }

    fun getProjects(
        sortBy: String, sortType: String, page: Int, size: Int
    ): Page<ProjectDto> {
        logger.info("Getting the projects")
        // Pagination and sorting
        val pageable: Pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortType), sortBy))
        var specification: Specification<Project> = Specification.where(null)
        specification = specification.and(specification.and(ProjectSpecification.statusIsTrue()))
        val projectEntities: Page<Project> = projectRepository.findAll(specification, pageable)
        return projectEntities.map { ProjectMapper.entityToDto(it) }
    }

    fun getProjectById(projectId: Long): ProjectDto {
        logger.info("Getting the project by id $projectId")
        // Get the user id from the token
        val userId = AuthUtil.getUserIdFromAuthToken() ?: throw EtnException(
            HttpStatus.UNAUTHORIZED, "Error: Unauthorized", "No autorizado"
        )
        // Validate that the user is the project owner, a project moderator or a project member
        if (projectOwnerRepository.findByProjectIdAndUserIdAndStatusIsTrue(
               projectId, userId
            ) == null && projectModeratorRepository.findByProjectIdAndUserIdAndStatusIsTrue(
                projectId, userId
            ) == null && projectMemberRepository.findByProjectIdAndUserIdAndStatusIsTrue(
                projectId, userId
            ) == null
        ) {
            throw EtnException(
                HttpStatus.FORBIDDEN,
                "Error: User is not the project owner or a project moderator",
                "El usuario no es el propietario del proyecto, un colaborador del proyecto o un miembro del proyecto"
            )
        }
        // Validate the project exists
        val projectEntity = projectRepository.findByProjectIdAndStatusIsTrue(projectId) ?: throw EtnException(
            HttpStatus.NOT_FOUND, "Error: Project not found", "Proyecto no encontrado"
        )
        return ProjectMapper.entityToDto(projectEntity)
    }


    fun createProject(newProjectDto: NewProjectDto) {
        // Validate the name is not empty
        if (newProjectDto.projectName.isEmpty() || newProjectDto.dateFrom.isEmpty() || newProjectDto.dateTo.isEmpty()) {
            throw EtnException(
                HttpStatus.BAD_REQUEST,
                "Error: At least one required field is blank",
                "Al menos un campo requerido está en blanco"
            )
        }
        // Validate the dates have the correct format
        try {
            val dateFrom = Timestamp.from(Instant.parse(newProjectDto.dateFrom))
            val dateTo = Timestamp.from(Instant.parse(newProjectDto.dateTo))
            logger.info("Date from: $dateFrom, Date to: $dateTo")
        } catch (e: Exception) {
            throw EtnException(
                HttpStatus.BAD_REQUEST, "Error: Date format is incorrect", "El formato de fecha es incorrecto"
            )
        }
        if (Timestamp.from(Instant.parse(newProjectDto.dateFrom))
                .after(Timestamp.from(Instant.parse(newProjectDto.dateTo)))
        ) {
            throw EtnException(
                HttpStatus.BAD_REQUEST, "Error: Date range is incorrect", "El rango de fechas es incorrecto"
            )
        }
        // Validate the project moderators are not empty and valid
        if (newProjectDto.projectModeratorIds.isEmpty()) {
            throw EtnException(
                HttpStatus.BAD_REQUEST, "Error: At least one moderator is required", "Se requiere al menos un moderador"
            )
        }
        // Validate the project members are not empty
        if (newProjectDto.projectMemberIds.isEmpty()) {
            throw EtnException(
                HttpStatus.BAD_REQUEST,
                "Error: At least one member is required",
                "Se requiere al menos un miembro del equipo"
            )
        }
        // Get the project owner id from the token
        val userId = AuthUtil.getUserIdFromAuthToken() ?: throw EtnException(
            HttpStatus.UNAUTHORIZED, "Error: Unauthorized", "No autorizado"
        )
        // Validate the project moderators exist
        if (userRepository.findAllByUserIdInAndStatusIsTrue(newProjectDto.projectModeratorIds).size != newProjectDto.projectModeratorIds.size) {
            throw EtnException(
                HttpStatus.BAD_REQUEST,
                "Error: At least one moderator is invalid",
                "Al menos un colaborador es inválido"
            )
        }
        // Validate the project members exist
        if (userRepository.findAllByUserIdInAndStatusIsTrue(newProjectDto.projectMemberIds).size != newProjectDto.projectMemberIds.size) {
            throw EtnException(
                HttpStatus.BAD_REQUEST, "Error: At least one member is invalid", "Al menos un miembro es inválido"
            )
        }
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

    fun updateProject(projectId: Long, projectDto: NewProjectDto) {
        // Validate the project exists
        val projectEntity = projectRepository.findByProjectIdAndStatusIsTrue(projectId) ?: throw EtnException(
            HttpStatus.NOT_FOUND, "Error: Project not found", "Proyecto no encontrado"
        )
        // Validate the project name is not empty
        if (projectDto.projectName.isEmpty()) {
            throw EtnException(
                HttpStatus.BAD_REQUEST, "Error: Project name is required", "Se requiere el nombre del proyecto"
            )
        }
        // Validate the project moderators are not empty and valid
        if (projectDto.projectModeratorIds.isEmpty()) {
            throw EtnException(
                HttpStatus.BAD_REQUEST,
                "Error: At least one moderator is required",
                "Se requiere al menos un colaborador"
            )
        }
        // Validate the project members are not empty
        if (projectDto.projectMemberIds.isEmpty()) {
            throw EtnException(
                HttpStatus.BAD_REQUEST,
                "Error: At least one member is required",
                "Se requiere al menos un miembro del equipo"
            )
        }
        // Validate the project moderators exist
        if (userRepository.findAllByUserIdInAndStatusIsTrue(projectDto.projectModeratorIds).size != projectDto.projectModeratorIds.size) {
            throw EtnException(
                HttpStatus.BAD_REQUEST,
                "Error: At least one moderator is invalid",
                "Al menos un colaborador es inválido"
            )
        }
        // Validate the project members exist
        if (userRepository.findAllByUserIdInAndStatusIsTrue(projectDto.projectMemberIds).size != projectDto.projectMemberIds.size) {
            throw EtnException(
                HttpStatus.BAD_REQUEST, "Error: At least one member is invalid", "Al menos un miembro es inválido"
            )
        }

        // Update the project
        projectEntity.projectName = projectDto.projectName
        projectEntity.projectDescription = projectDto.projectDescription
        projectRepository.save(projectEntity)
        logger.info("Project updated with id $projectId")

        // Delete previous project moderators changing their status to false if they are different
        val projectModeratorEntities = projectModeratorRepository.findAllByProjectIdAndStatusIsTrue(projectId)
        // If they are different, update the project moderators
        if (projectModeratorEntities.map { it.userId }.toSet() != projectDto.projectModeratorIds.map { it }.toSet()) {
            projectModeratorEntities.forEach {
                it.status = false
                projectModeratorRepository.save(it)
            }
            // Create the new project moderators
            projectDto.projectModeratorIds.forEach { moderatorId ->
                val projectModeratorEntity = ProjectModerator()
                projectModeratorEntity.projectId = projectEntity.projectId
                projectModeratorEntity.userId = moderatorId
                projectModeratorRepository.save(projectModeratorEntity)
            }
        }

        // Delete previous project members changing their status to false
        val projectMemberEntities = projectMemberRepository.findAllByProjectIdAndStatusIsTrue(projectId)
        // If they are different, update the project members
        if (projectMemberEntities.map { it.userId }.toSet() != projectDto.projectMemberIds.map { it }.toSet()) {
            // Validate that none of the members have been assigned to a task
            val taskEntities = taskRepository.findAllByProjectIdAndStatusIsTrue(projectId)
            val taskAssigneeEntities = taskAssigneeRepository.findAllByTaskIdInAndStatusIsTrue(taskEntities.map { it.taskId })
            if (taskAssigneeEntities.isNotEmpty()) {
                throw EtnException(
                    HttpStatus.BAD_REQUEST,
                    "Error: At least one member has been assigned to a task, cannot delete",
                    "Al menos un miembro ha sido asignado a una tarea, no se puede eliminar"
                )
            }
            projectMemberEntities.forEach {
                it.status = false
                projectMemberRepository.save(it)
            }
            // Create the new project members
            projectDto.projectMemberIds.forEach { memberId ->
                val projectMemberEntity = ProjectMember()
                projectMemberEntity.projectId = projectEntity.projectId
                projectMemberEntity.userId = memberId
                projectMemberRepository.save(projectMemberEntity)
            }
        }
        logger.info("Project moderators and members updated for project $projectId")
    }

    fun deleteProject(projectId: Long) {
        // Validate the project exists
        val projectEntity = projectRepository.findByProjectIdAndStatusIsTrue(projectId) ?: throw EtnException(
            HttpStatus.NOT_FOUND, "Error: Project not found", "Proyecto no encontrado"
        )
        // Delete the project changing its status to false
        projectEntity.status = false
        projectRepository.save(projectEntity)
        logger.info("Project deleted with id $projectId")
    }

    fun getProjectTasks(
        projectId: Long, sortBy: String, sortType: String, page: Int, size: Int, keyword: String?, statuses: List<String>?, dateFrom: String?, dateTo: String?
    ): Page<TaskDto> {
        logger.info("Getting the tasks for project $projectId")
        // Validate the project exists
        projectRepository.findByProjectIdAndStatusIsTrue(projectId) ?: throw EtnException(
            HttpStatus.NOT_FOUND, "Error: Project not found", "Proyecto no encontrado"
        )
        // Pagination and sorting
        val pageable: Pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortType), sortBy))
        var specification: Specification<Task> = Specification.where(null)
        specification = specification.and(specification.and(TaskSpecification.statusIsTrue()))
        specification = specification.and(specification.and(TaskSpecification.projectId(projectId.toInt())))

        if (!keyword.isNullOrEmpty() && keyword.isNotBlank()) {
            specification = if (keyword.toIntOrNull() != null) {
                specification.and(specification.and(TaskSpecification.taskPriority(keyword)))
            } else {
                specification.and(specification.and(TaskSpecification.taskKeyword(keyword)))
            }
        }

        if (!statuses.isNullOrEmpty()) {
            val currentDate = if (statuses.contains("VENCIDO")) Timestamp.from(Instant.now()) else null
            specification = specification.and(TaskSpecification.taskStatuses(statuses, currentDate))
        }

        try {
            val newDateFrom = if (!dateFrom.isNullOrEmpty()) Timestamp.from(Instant.parse(dateFrom)) else Timestamp.from(Instant.parse("2024-01-01T00:00:00Z"))
            val newDateTo = if (!dateTo.isNullOrEmpty()) Timestamp.from(Instant.parse(dateTo)) else Timestamp.from(Instant.parse("2050-01-01T00:00:00Z"))
            if (newDateFrom.after(newDateTo)) {
                specification = specification.and(specification.and(TaskSpecification.dateBetween(newDateFrom, newDateTo)))
            }
        } catch (
            e: Exception
        ) {
            throw EtnException(
                HttpStatus.BAD_REQUEST, "Error: Date format is incorrect", "El formato de fecha es incorrecto"
            )
        }

        val taskEntities: Page<Task> = taskRepository.findAll(specification, pageable)
        return taskEntities.map { TaskMapper.entityToDto(it) }
    }
}