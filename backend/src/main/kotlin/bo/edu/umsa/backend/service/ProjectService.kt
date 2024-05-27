package bo.edu.umsa.backend.service

import bo.edu.umsa.backend.dto.*
import bo.edu.umsa.backend.entity.*
import bo.edu.umsa.backend.exception.EtnException
import bo.edu.umsa.backend.mapper.ProjectMapper
import bo.edu.umsa.backend.mapper.ProjectPartialMapper
import bo.edu.umsa.backend.mapper.TaskPartialMapper
import bo.edu.umsa.backend.repository.*
import bo.edu.umsa.backend.specification.ProjectSpecification
import bo.edu.umsa.backend.specification.TaskSpecification
import bo.edu.umsa.backend.util.AuthUtil
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.http.HttpStatus
import org.springframework.scheduling.annotation.Scheduled
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
    private val emailService: EmailService,
    private val firebaseMessagingService: FirebaseMessagingService,
    private val firebaseTokenRepository: FirebaseTokenRepository,
    private val notificationRepository: NotificationRepository
) {
    companion object {
        private val logger = LoggerFactory.getLogger(ProjectService::class.java)
    }

    fun getAllProjects(): List<ProjectPartialDto> {
        logger.info("Getting all projects")
        // Check if the user is owner, moderator or member of the project
        val userId = AuthUtil.getUserIdFromAuthToken() ?: throw EtnException(HttpStatus.UNAUTHORIZED, "Error: Unauthorized", "No autorizado")
        val projectOwnerEntities = projectOwnerRepository.findAllByUserIdAndStatusIsTrue(userId)
        val projectModeratorEntities = projectModeratorRepository.findAllByUserIdAndStatusIsTrue(userId)
        // Get the projects where the user is owner, moderator or member
        val projectEntities = projectRepository.findAllByProjectIdInAndProjectEndDateIsNullAndStatusIsTrueOrderByProjectIdDesc(
            projectOwnerEntities.map { it.projectId } +
                    projectModeratorEntities.map { it.projectId }
        )
        return projectEntities.map { ProjectPartialMapper.entityToDto(it) }
    }

    fun getProjects(
        sortBy: String,
        sortType: String,
        page: Int,
        size: Int,
        keyword: String?,
        ): Page<ProjectPartialDto> {
        logger.info("Getting the projects")
        // Pagination and sorting
        val pageable: Pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortType), sortBy))
        var specification: Specification<Project> = Specification.where(null)
        specification = specification.and(ProjectSpecification.statusIsTrue())

        if (!keyword.isNullOrEmpty() && keyword.isNotBlank()) {
            specification = specification.and(ProjectSpecification.projectKeyword(keyword))
        }
        val projectEntities: Page<Project> = projectRepository.findAll(specification, pageable)
        return projectEntities.map { ProjectPartialMapper.entityToDto(it) }
    }

    fun getProjectById(projectId: Long): ProjectDto {
        logger.info("Getting the project by id $projectId")
        // Get the user id from the token
        val userId = AuthUtil.getUserIdFromAuthToken() ?: throw EtnException(HttpStatus.UNAUTHORIZED, "Error: Unauthorized", "No autorizado")
        // Validate that the user is the project owner, a project moderator or a project member
        if (projectOwnerRepository.findByProjectIdAndUserIdAndStatusIsTrue(projectId, userId) == null && projectModeratorRepository.findByProjectIdAndUserIdAndStatusIsTrue(projectId, userId) == null && projectMemberRepository.findByProjectIdAndUserIdAndStatusIsTrue(projectId, userId) == null) {
            throw EtnException(HttpStatus.FORBIDDEN, "Error: User is not the project owner or a project moderator", "El usuario no es el propietario del proyecto, un colaborador del proyecto o un miembro del proyecto")
        }
        // Validate the project exists
        val projectEntity = projectRepository.findByProjectIdAndStatusIsTrue(projectId)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: Project not found", "Proyecto no encontrado")
        return ProjectMapper.entityToDto(projectEntity)
    }


    fun createProject(newProjectDto: NewProjectDto) {
        // Validate the name is not empty
        if (newProjectDto.projectName.trim().isEmpty() || newProjectDto.projectDateFrom.trim().isEmpty() || newProjectDto.projectDateTo.trim().isEmpty() || newProjectDto.projectObjective.trim().isEmpty()) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: At least one required field is blank", "Al menos un campo requerido está en blanco")
        }
        // Validate the dates have the correct format
        try {
            Timestamp.from(Instant.parse(newProjectDto.projectDateFrom))
            Timestamp.from(Instant.parse(newProjectDto.projectDateTo))
        } catch (e: Exception) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Date format is incorrect", "El formato de fecha es incorrecto")
        }
        if (Timestamp.from(Instant.parse(newProjectDto.projectDateFrom)).after(Timestamp.from(Instant.parse(newProjectDto.projectDateTo)))) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Date range is incorrect", "El rango de fechas es incorrecto")
        }
        // Validate the end date is after current date
        if (Timestamp.from(Instant.parse(newProjectDto.projectDateTo)).before(Timestamp.from(Instant.now()))) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: End date is before current date", "La fecha de finalización es anterior a la fecha actual")
        }
        // Validate the project moderators are not empty and valid
        if (newProjectDto.projectModeratorIds.isEmpty()) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: At least one moderator is required", "Se requiere al menos un colaborador")
        }
        // Validate the project members are not empty
        if (newProjectDto.projectMemberIds.isEmpty()) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: At least one member is required", "Se requiere al menos un miembro del equipo")
        }
        // Get the project owner id from the token
        val userId = AuthUtil.getUserIdFromAuthToken() ?: throw EtnException(HttpStatus.UNAUTHORIZED, "Error: Unauthorized", "No autorizado")
        // Validate the project moderators exist
        if (userRepository.findAllByUserIdInAndStatusIsTrue(newProjectDto.projectModeratorIds).size != newProjectDto.projectModeratorIds.size) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: At least one moderator is invalid", "Al menos un colaborador es inválido")
        }
        // Validate the project members exist
        if (userRepository.findAllByUserIdInAndStatusIsTrue(newProjectDto.projectMemberIds).size != newProjectDto.projectMemberIds.size) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: At least one member is invalid", "Al menos un miembro es inválido")
        }
        logger.info("Creating a new project with owner id $userId")

        // Create the project
        val projectEntity = Project()
        projectEntity.projectName = newProjectDto.projectName
        projectEntity.projectDescription = newProjectDto.projectDescription
        projectEntity.projectObjective = newProjectDto.projectObjective
        projectEntity.projectDateFrom = Timestamp.from(Instant.parse(newProjectDto.projectDateFrom))
        projectEntity.projectDateTo = Timestamp.from(Instant.parse(newProjectDto.projectDateTo).plusSeconds(60 * 60 * 24 - 1))
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

    fun updateProject(
        projectId: Long,
        projectDto: NewProjectDto
    ) {
        // Validate the name is not empty
        if (projectDto.projectName.trim().isEmpty() || projectDto.projectDateFrom.trim().isEmpty() || projectDto.projectDateTo.trim().isEmpty() || projectDto.projectObjective.trim().isEmpty()) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: At least one required field is blank", "Al menos un campo requerido está en blanco")
        }
        // Validate the dates have the correct format
        try {
            Timestamp.from(Instant.parse(projectDto.projectDateFrom))
            Timestamp.from(Instant.parse(projectDto.projectDateTo))
        } catch (e: Exception) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Date format is incorrect", "El formato de fecha es incorrecto")
        }
        if (Timestamp.from(Instant.parse(projectDto.projectDateFrom)).after(Timestamp.from(Instant.parse(projectDto.projectDateTo)))) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Date range is incorrect", "El rango de fechas es incorrecto")
        }
        // Validate the end date is after current date
        if (Timestamp.from(Instant.parse(projectDto.projectDateTo)).before(Timestamp.from(Instant.now()))) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: End date is before current date", "La fecha de finalización es anterior a la fecha actual")
        }
        // Validate the project exists
        val projectEntity = projectRepository.findByProjectIdAndStatusIsTrue(projectId)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: Project not found", "Proyecto no encontrado")
        // Validate the project is not closed
        if (projectEntity.projectEndDate != null) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Project is closed", "El proyecto está cerrado")
        }
        // Validate the project moderators are not empty and valid
        if (projectDto.projectModeratorIds.isEmpty()) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: At least one moderator is required", "Se requiere al menos un colaborador")
        }
        // Validate the project members are not empty
        if (projectDto.projectMemberIds.isEmpty()) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: At least one member is required", "Se requiere al menos un miembro del equipo")
        }
        // Validate the project moderators exist
        if (userRepository.findAllByUserIdInAndStatusIsTrue(projectDto.projectModeratorIds).size != projectDto.projectModeratorIds.size) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: At least one moderator is invalid", "Al menos un colaborador es inválido")
        }
        // Validate the project members exist
        if (userRepository.findAllByUserIdInAndStatusIsTrue(projectDto.projectMemberIds).size != projectDto.projectMemberIds.size) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: At least one member is invalid", "Al menos un miembro es inválido")
        }
        // Update the project
        projectEntity.projectName = projectDto.projectName
        projectEntity.projectDescription = projectDto.projectDescription
        projectEntity.projectObjective = projectDto.projectObjective
        projectEntity.projectDateFrom = Timestamp.from(Instant.parse(projectDto.projectDateFrom))
        projectEntity.projectDateTo = Timestamp.from(Instant.parse(projectDto.projectDateTo).plusSeconds(60 * 60 * 24 - 1))
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
                throw EtnException(HttpStatus.BAD_REQUEST, "Error: At least one member has been assigned to a task, cannot delete", "Al menos un miembro ha sido asignado a una tarea, no se puede eliminar")
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
        val projectEntity = projectRepository.findByProjectIdAndStatusIsTrue(projectId)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: Project not found", "Proyecto no encontrado")
        // Validate the project does not have tasks
        val taskEntities = taskRepository.findAllByProjectIdAndStatusIsTrue(projectId)
        if (taskEntities.isNotEmpty()) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Project has tasks", "El proyecto tiene tareas")
        }
        // Validate the project is not closed
        if (projectEntity.projectEndDate != null) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Project is closed", "El proyecto está cerrado")
        }
        // Delete the project changing its status to false
        projectEntity.status = false
        projectRepository.save(projectEntity)
        logger.info("Project deleted with id $projectId")
    }

    fun closeProject(projectId: Long, closeProjectDto: CloseProjectDto) {
        // Validate the close message is not empty
        if (closeProjectDto.projectCloseMessage.trim().isEmpty()) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Close message is required", "Se requiere un mensaje de cierre")
        }
        // Validate the project exists
        val projectEntity = projectRepository.findByProjectIdAndStatusIsTrue(projectId)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: Project not found", "Proyecto no encontrado")
        // Validate the project is not closed
        if (projectEntity.projectEndDate != null) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Project is closed", "El proyecto está cerrado")
        }
        // Validate the project does not have uncompleted tasks, by checking the status of the tasks
        val taskEntities = taskRepository.findAllByProjectIdAndStatusIsTrue(projectId)
        if (taskEntities.isEmpty()) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Project has no tasks", "El proyecto no tiene tareas")
        }
        if (taskEntities.any { it.taskStatusId != 3 }) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Project has uncompleted tasks", "El proyecto tiene tareas incompletas")
        }
        // Validate the project owner or a project moderator is closing the project
        val userId = AuthUtil.getUserIdFromAuthToken() ?: throw EtnException(HttpStatus.UNAUTHORIZED, "Error: Unauthorized", "No autorizado")
        if (projectOwnerRepository.findByProjectIdAndUserIdAndStatusIsTrue(projectId, userId) == null && projectModeratorRepository.findByProjectIdAndUserIdAndStatusIsTrue(projectId, userId) == null) {
            throw EtnException(HttpStatus.FORBIDDEN, "Error: User is not the project owner or a project moderator", "El usuario no es el propietario del proyecto o un colaborador del proyecto")
        }
        // Close the project
        projectEntity.projectEndDate = Timestamp.from(Instant.now())
        projectEntity.projectCloseMessage = closeProjectDto.projectCloseMessage
        projectRepository.save(projectEntity)
        logger.info("Project closed with id $projectId")

        // Send notification to the project owner and moderators
        val projectOwnerEntities = projectOwnerRepository.findAllByProjectIdAndStatusIsTrue(projectId)
        val projectModeratorEntities = projectModeratorRepository.findAllByProjectIdAndStatusIsTrue(projectId)
        projectOwnerEntities.forEach { projectOwnerEntity ->
            val projectName = projectRepository.findByProjectIdAndStatusIsTrue(projectId)!!.projectName
            val ownerEmail = userRepository.findByUserIdAndStatusIsTrue(projectOwnerEntity.userId.toLong())!!.email
            logger.info("Sending notification to project owner $ownerEmail")
            val ownerTokens = firebaseTokenRepository.findAllByUserIdAndStatusIsTrue(projectOwnerEntity.userId.toLong()).map { it.firebaseToken }
            val ownerMessageTittle = "Proyecto cerrado"
            val ownerMessageBody = "El proyecto '$projectName' ha sido cerrado"
            val notificationEntity = Notification()
            notificationEntity.messageTitle = ownerMessageTittle
            notificationEntity.messageBody = ownerMessageBody
            notificationEntity.userId = projectOwnerEntity.userId
            notificationRepository.save(notificationEntity)
            emailService.sendEmail(ownerEmail, ownerMessageTittle, ownerMessageBody)
            ownerTokens.forEach { token ->
                firebaseMessagingService.sendNotification(token, ownerMessageTittle, ownerMessageBody)
            }
        }

        projectModeratorEntities.forEach { projectModeratorEntity ->
            val projectName = projectRepository.findByProjectIdAndStatusIsTrue(projectId)!!.projectName
            val moderatorEmail = userRepository.findByUserIdAndStatusIsTrue(projectModeratorEntity.userId.toLong())!!.email
            logger.info("Sending notification to project moderator $moderatorEmail")
            val moderatorTokens = firebaseTokenRepository.findAllByUserIdAndStatusIsTrue(projectModeratorEntity.userId.toLong()).map { it.firebaseToken }
            val moderatorMessageTittle = "Proyecto cerrado"
            val moderatorMessageBody = "El proyecto '$projectName' ha sido cerrado"
            val notificationEntity = Notification()
            notificationEntity.messageTitle = moderatorMessageTittle
            notificationEntity.messageBody = moderatorMessageBody
            notificationEntity.userId = projectModeratorEntity.userId
            notificationRepository.save(notificationEntity)
            emailService.sendEmail(moderatorEmail, moderatorMessageTittle, moderatorMessageBody)
            moderatorTokens.forEach { token ->
                firebaseMessagingService.sendNotification(token, moderatorMessageTittle, moderatorMessageBody)
            }
        }

    }

    fun getProjectTasks(
        projectId: Long,
        sortBy: String,
        sortType: String,
        page: Int,
        size: Int,
        keyword: String?,
        statuses: List<String>?,
        priorities: List<String>?,
        dateFrom: String?,
        dateTo: String?
    ): Page<TaskPartialDto> {
        logger.info("Getting the tasks for project $projectId")
        // Validate the project exists
        projectRepository.findByProjectIdAndStatusIsTrue(projectId)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: Project not found", "Proyecto no encontrado")
        // Pagination and sorting
        val pageable: Pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortType), sortBy))
        var specification: Specification<Task> = Specification.where(null)
        specification = specification.and(TaskSpecification.statusIsTrue())
        specification = specification.and(TaskSpecification.projectId(projectId.toInt()))

        if (!keyword.isNullOrEmpty() && keyword.isNotBlank()) {
            specification = specification.and(TaskSpecification.taskKeyword(keyword))
        }

        if (!statuses.isNullOrEmpty()) {
            specification = specification.and(TaskSpecification.taskStatuses(statuses))
        }

        if (!priorities.isNullOrEmpty()) {
            specification = specification.and(TaskSpecification.taskPriorities(priorities))
        }

        try {
            val newDateFrom = if (!dateFrom.isNullOrEmpty()) Timestamp.from(Instant.parse(dateFrom)) else Timestamp.from(Instant.parse("2024-01-01T00:00:00Z"))
            val newDateTo = if (!dateTo.isNullOrEmpty()) Timestamp.from(Instant.parse(dateTo)) else Timestamp.from(Instant.parse("2050-01-01T00:00:00Z"))
            if (newDateFrom.after(newDateTo)) {
                specification = specification.and(TaskSpecification.dateBetween(newDateFrom, newDateTo))
            }
        } catch (e: Exception) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Date format is incorrect", "El formato de fecha es incorrecto")
        }

        val taskEntities: Page<Task> = taskRepository.findAll(specification, pageable)
        return taskEntities.map { TaskPartialMapper.entityToDto(it) }
    }

    @Scheduled(cron = "0 0 20 * * *")
    fun sendNotification() {
        // find all project wich PROJECT END DATE IS in the next 24 hours
        logger.info("Starting sending notifications")
        val secondsToAdd: Long = 60 * 60 * 24 // 24 hours
        val projectEntities = projectRepository.findAllByProjectDateToBetweenAndProjectEndDateIsNullAndStatusIsTrueOrderByProjectDateTo(Timestamp.from(Instant.now()), Timestamp.from(Instant.now().plusSeconds(secondsToAdd)))

        val projectOwnerEntities = projectOwnerRepository.findAllByProjectIdInAndStatusIsTrue(projectEntities.map { it.projectId })
        val projectModeratorEntities = projectModeratorRepository.findAllByProjectIdInAndStatusIsTrue(projectEntities.map { it.projectId })

        projectOwnerEntities.forEach { projectOwnerEntity ->
            val projectName = projectRepository.findByProjectIdAndStatusIsTrue(projectOwnerEntity.projectId.toLong())!!.projectName
            val ownerEmail = userRepository.findByUserIdAndStatusIsTrue(projectOwnerEntity.userId.toLong())!!.email
            logger.info("Sending notification to project owner ${ownerEmail}")
            val ownerTokens = firebaseTokenRepository.findAllByUserIdAndStatusIsTrue(projectOwnerEntity.userId.toLong()).map { it.firebaseToken }
            val ownerMessageTittle = "Recordatorio: Proyecto por finalizar"
            val ownerMessageBody = "El proyecto: '$projectName' en el que participas como propietario está por finalizar en las próximas 24 horas. Por favor, asegúrate de que todas las tareas estén completadas."
            val notificationEntity = Notification()
            notificationEntity.messageTitle = ownerMessageTittle
            notificationEntity.messageBody = ownerMessageBody
            notificationEntity.userId = projectOwnerEntity.userId
            notificationRepository.save(notificationEntity)
            emailService.sendEmail(ownerEmail, ownerMessageTittle, ownerMessageBody)
            ownerTokens.forEach { token ->
                    firebaseMessagingService.sendNotification(token, ownerMessageTittle, ownerMessageBody)
            }
        }

        projectModeratorEntities.forEach { projectModeratorEntity ->
            val projectName = projectRepository.findByProjectIdAndStatusIsTrue(projectModeratorEntity.projectId.toLong())!!.projectName
            val moderatorEmail = userRepository.findByUserIdAndStatusIsTrue(projectModeratorEntity.userId.toLong())!!.email
            logger.info("Sending notification to project moderator ${moderatorEmail}")
            val moderatorTokens = firebaseTokenRepository.findAllByUserIdAndStatusIsTrue(projectModeratorEntity.userId.toLong()).map { it.firebaseToken }
            val moderatorMessageTittle = "Recordatorio: Proyecto por finalizar"
            val moderatorMessageBody = "El proyecto: '$projectName' en el que participas como colaborador está por finalizar en las próximas 24 horas. Por favor, asegúrate de que todas las tareas estén completadas."
            val notificationEntity = Notification()
            notificationEntity.messageTitle = moderatorMessageTittle
            notificationEntity.messageBody = moderatorMessageBody
            notificationEntity.userId = projectModeratorEntity.userId
            emailService.sendEmail(moderatorEmail, moderatorMessageTittle, moderatorMessageBody)
            moderatorTokens.forEach { token ->
                    firebaseMessagingService.sendNotification(token, moderatorMessageTittle, moderatorMessageBody)
            }
        }
    }
}