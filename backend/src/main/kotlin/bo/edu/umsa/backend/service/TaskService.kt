package bo.edu.umsa.backend.service

import bo.edu.umsa.backend.dto.*
import bo.edu.umsa.backend.entity.*
import bo.edu.umsa.backend.exception.EtnException
import bo.edu.umsa.backend.mapper.*
import bo.edu.umsa.backend.repository.*
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
class TaskService @Autowired constructor(
    private val fileRepository: FileRepository,
    private val projectRepository: ProjectRepository,
    private val projectOwnerRepository: ProjectOwnerRepository,
    private val projectModeratorRepository: ProjectModeratorRepository,
    private val projectMemberRepository: ProjectMemberRepository,
    private val userRepository: UserRepository,
    private val taskRepository: TaskRepository,
    private val taskAssigneeRepository: TaskAssigneeRepository,
    private val taskHistoryRepository: TaskHistoryRepository,
    private val taskFileRepository: TaskFileRepository,
    private val taskPriorityRepository: TaskPriorityRepository,
    private val taskStatusRepository: TaskStatusRepository,
    private val emailService: EmailService,
    private val firebaseMessagingService: FirebaseMessagingService,
    private val firebaseTokenRepository: FirebaseTokenRepository,
    private val notificationRepository: NotificationRepository
) {
    companion object {
        private val logger = LoggerFactory.getLogger(TaskService::class.java)
    }

    fun getAllStatuses(): List<TaskStatusDto> {
        logger.info("Getting all task statuses")
        val statuses = taskStatusRepository.findAllByStatusIsTrueOrderByTaskStatusId()
        return statuses.map { TaskStatusMapper.entityToDto(it) }
    }

    fun getAllPriorities(): List<TaskPriorityDto> {
        logger.info("Getting all task priorities")
        val priorities = taskPriorityRepository.findAllByStatusIsTrueOrderByTaskPriorityId()
        return priorities.map { TaskPriorityMapper.entityToDto(it) }
    }

    fun getTasks(
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
        val userId = AuthUtil.getUserIdFromAuthToken() ?: throw EtnException(HttpStatus.UNAUTHORIZED, "Error: Unauthorized", "No autorizado")
        logger.info("Getting the tasks for user $userId")
        // Validate the user exists
        userRepository.findByUserIdAndStatusIsTrue(userId)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: User not found", "Usuario no encontrado")

        // Pagination and sorting
        val pageable: Pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortType), sortBy))
        var specification: Specification<Task> = Specification.where(null)
        specification = specification.and(TaskSpecification.statusIsTrue())
        specification = specification.and(TaskSpecification.taskAssignee(userId.toInt()))

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

    fun getTaskById(taskId: Long): TaskDto {
        val userId = AuthUtil.getUserIdFromAuthToken() ?: throw EtnException(HttpStatus.UNAUTHORIZED, "Error: Unauthorized", "No autorizado")
        logger.info("Getting the task with id $taskId for user $userId")
        // Validate the user exists
        userRepository.findByUserIdAndStatusIsTrue(userId)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: User not found", "Usuario no encontrado")
        // Validate the task exists
        val taskEntity = taskRepository.findByTaskIdAndStatusIsTrue(taskId)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: Task not found", "Tarea no encontrada")
        // Validate that the user is the project owner, a project moderator or a project member
        if (projectOwnerRepository.findByProjectIdAndUserIdAndStatusIsTrue(taskEntity.projectId.toLong(), userId) == null && projectModeratorRepository.findByProjectIdAndUserIdAndStatusIsTrue(taskEntity.projectId.toLong(), userId) == null && projectMemberRepository.findByProjectIdAndUserIdAndStatusIsTrue(taskEntity.projectId.toLong(), userId) == null) {
            throw EtnException(HttpStatus.FORBIDDEN, "Error: User is not the project owner or a project moderator", "El usuario no es el propietario del proyecto, un colaborador del proyecto o un miembro del proyecto")
        }
        return TaskMapper.entityToDto(taskEntity)
    }

    fun createTask(newTaskDto: NewTaskDto) {
        // Validate the name, description, and dueDate are not empty
        if (newTaskDto.taskName.trim().isEmpty() || newTaskDto.taskDueDate.trim().isEmpty()) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: At least one required field is blank", "Al menos un campo requerido está en blanco")
        }
        // Validate the dates have the correct format
        try {
            Timestamp.from(Instant.parse(newTaskDto.taskDueDate))
        } catch (e: Exception) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Date format is incorrect", "El formato de fecha es incorrecto")
        }
        if (Timestamp.from(Instant.parse(newTaskDto.taskDueDate)).before(Timestamp.from(Instant.now()))) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: DueDate is in the past", "La fecha límite está en el pasado")
        }
        // Validate the task priority exists
        taskPriorityRepository.findByTaskPriorityIdAndStatusIsTrue(newTaskDto.taskPriorityId.toLong())
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: Task priority not found", "Prioridad de la tarea no encontrada")

        // Validate the task assignees are not empty
        if (newTaskDto.taskAssigneeIds.isEmpty()) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: At least one assignee is required", "Se requiere al menos un miembro asignado")
        }
        // Get the project owner id from the token
        val userId = AuthUtil.getUserIdFromAuthToken() ?: throw EtnException(HttpStatus.UNAUTHORIZED, "Error: Unauthorized", "No autorizado")
        // Validate the project exists
        val projectEntity = projectRepository.findByProjectIdAndStatusIsTrue(newTaskDto.projectId.toLong())
            ?: throw EtnException(HttpStatus.BAD_REQUEST, "Error: Project does not exist", "El proyecto no existe")
        // Validate the project is not closed
        if (projectEntity.projectEndDate != null) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Project is closed", "El proyecto está cerrado")
        }
        // Validate the task dueDate is between the project date from and date to
        if (Timestamp.from(Instant.parse(newTaskDto.taskDueDate)).before(projectEntity.projectDateFrom) || Timestamp.from(Instant.parse(newTaskDto.taskDueDate)).after(projectEntity.projectDateTo)) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Task dueDate is not between the project date from and date to", "La fecha límite de la tarea no está entre la fecha de inicio y la fecha de finalización del proyecto")
        }
        // Validate the task assignees exist
        if (userRepository.findAllByUserIdInAndStatusIsTrue(newTaskDto.taskAssigneeIds).size != newTaskDto.taskAssigneeIds.size) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: At least one assignee is invalid", "Al menos un miembro asignado es inválido")
        }
        // Validate that the user is the project owner or a project moderator
        if (projectOwnerRepository.findByProjectIdAndUserIdAndStatusIsTrue(newTaskDto.projectId.toLong(), userId) == null && projectModeratorRepository.findByProjectIdAndUserIdAndStatusIsTrue(newTaskDto.projectId.toLong(), userId) == null) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: User is not the project owner or a project moderator", "El usuario no es el propietario del proyecto o un colaborador del proyecto")
        }
        // Validate the assignees are project members
        if (projectMemberRepository.findAllByProjectIdAndUserIdInAndStatusIsTrue(newTaskDto.projectId.toLong(), newTaskDto.taskAssigneeIds).size != newTaskDto.taskAssigneeIds.size) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: At least one assignee is not a project member", "Al menos un miembro asignado no es un miembro del proyecto")
        }
        // Validate that the task file ids are valid
        if (newTaskDto.taskFileIds.isNotEmpty()) {
            // Validate the task files exist
            if (fileRepository.findAllByFileIdInAndStatusIsTrue(newTaskDto.taskFileIds).size != newTaskDto.taskFileIds.size) {
                throw EtnException(HttpStatus.BAD_REQUEST, "Error: At least one task file is invalid", "Al menos un archivo adjunto es inválido")
            }
        }

        logger.info("Creating a new task with owner id $userId")
        // Create the task
        val taskEntity = Task()
        taskEntity.projectId = newTaskDto.projectId
        taskEntity.taskStatusId = 1
        taskEntity.taskPriorityId = newTaskDto.taskPriorityId
        taskEntity.taskName = newTaskDto.taskName
        taskEntity.taskDescription = newTaskDto.taskDescription
        taskEntity.taskDueDate = Timestamp.from(Instant.parse(newTaskDto.taskDueDate))
        taskRepository.save(taskEntity)
        logger.info("Task created with id ${taskEntity.taskId}")

        // Create the task assignees
        newTaskDto.taskAssigneeIds.forEach { assigneeId ->
            val taskAssigneeEntity = TaskAssignee()
            taskAssigneeEntity.taskId = taskEntity.taskId
            taskAssigneeEntity.userId = assigneeId
            taskAssigneeRepository.save(taskAssigneeEntity)
        }
        logger.info("Task assignees created for task ${taskEntity.taskId}")

        // Create the task files
        newTaskDto.taskFileIds.forEach { fileId ->
            val taskFileEntity = TaskFile()
            taskFileEntity.taskId = taskEntity.taskId
            taskFileEntity.fileId = fileId
            taskFileRepository.save(taskFileEntity)
        }
        logger.info("Task files created for task ${taskEntity.taskId}")

        // Create the task history
        val taskHistoryEntity = TaskHistory()
        taskHistoryEntity.taskId = taskEntity.taskId
        taskHistoryEntity.userId = userId.toInt()
        taskHistoryEntity.fieldName = TaskHistoryType.TAREA
        taskHistoryEntity.previousValue = ""
        taskHistoryEntity.newValue = "Tarea creada"
        taskHistoryRepository.save(taskHistoryEntity)

        // Create the task history for the assignees
        val taskHistoryAssigneeEntities = TaskHistory()
        taskHistoryAssigneeEntities.taskId = taskEntity.taskId
        taskHistoryAssigneeEntities.userId = userId.toInt()
        taskHistoryAssigneeEntities.fieldName = TaskHistoryType.RESPONSABLE
        taskHistoryAssigneeEntities.previousValue = ""

        // Get the assignee names
        val assigneeNames = userRepository.findAllByUserIdInAndStatusIsTrue(newTaskDto.taskAssigneeIds).map { it.firstName + " " + it.lastName }
        taskHistoryAssigneeEntities.newValue = assigneeNames.joinToString(", ")
        taskHistoryRepository.save(taskHistoryAssigneeEntities)

        // Send the notification to the assignees
        val taskAssigneeEntities = taskAssigneeRepository.findAllByTaskIdAndStatusIsTrue(taskEntity.taskId.toLong())

        taskAssigneeEntities.forEach { taskAssigneeEntity ->
            val assigneeEmail = userRepository.findByUserIdAndStatusIsTrue(taskAssigneeEntity.userId.toLong())!!.email
            val assigneeTokens = firebaseTokenRepository.findAllByUserIdAndStatusIsTrue(taskAssigneeEntity.userId.toLong()).map { it.firebaseToken }
            val assigneeMessageTittle = "Tarea asignada"
            val assigneeMessageBody = "Se le ha asignado la tarea '${taskEntity.taskName}' en el proyecto '${projectEntity.projectName}'"
            val notificationEntity = Notification()
            notificationEntity.messageTitle = assigneeMessageTittle
            notificationEntity.messageBody = assigneeMessageBody
            notificationEntity.userId = taskAssigneeEntity.userId
            notificationRepository.save(notificationEntity)
            emailService.sendEmail(assigneeEmail, assigneeMessageTittle, assigneeMessageBody)
            assigneeTokens.forEach { token ->
                    firebaseMessagingService.sendNotification(token, assigneeMessageTittle, assigneeMessageBody)
            }
        }
    }

    fun updateTask(
        taskId: Long,
        newTaskDto: NewTaskDto
    ) {
        // Validate the name, description, and dueDate are not empty
        if (newTaskDto.taskName.trim().isEmpty() || newTaskDto.taskDueDate.trim().isEmpty()) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: At least one required field is blank", "Al menos un campo requerido está en blanco")
        }
        // Validate the dates have the correct format
        try {
            Timestamp.from(Instant.parse(newTaskDto.taskDueDate))
        } catch (e: Exception) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Date format is incorrect", "El formato de fecha es incorrecto")
        }
        if (Timestamp.from(Instant.parse(newTaskDto.taskDueDate)).before(Timestamp.from(Instant.now()))) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: DueDate is in the past", "La fecha límite está en el pasado")
        }
        // Validate the task exists
        val taskEntity = taskRepository.findByTaskIdAndStatusIsTrue(taskId)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: Task not found", "Tarea no encontrada")
        // Validate the task is not completed
        if (taskEntity.taskEndDate != null) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Task is completed", "La tarea está completada")
        }
        // Validate the task priority exists
        taskPriorityRepository.findByTaskPriorityIdAndStatusIsTrue(newTaskDto.taskPriorityId.toLong())
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: Task priority not found", "Prioridad de la tarea no encontrada")

        // Validate the task assignees are not empty
        if (newTaskDto.taskAssigneeIds.isEmpty()) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: At least one assignee is required", "Se requiere al menos un miembro asignado")
        }
        // Get the project owner id from the token
        val userId = AuthUtil.getUserIdFromAuthToken() ?: throw EtnException(HttpStatus.UNAUTHORIZED, "Error: Unauthorized", "No autorizado")
        // Validate the project exists
        val projectEntity = projectRepository.findByProjectIdAndStatusIsTrue(newTaskDto.projectId.toLong())
            ?: throw EtnException(HttpStatus.BAD_REQUEST, "Error: Project does not exist", "El proyecto no existe")
        // Validate the project is not closed
        if (projectEntity.projectEndDate != null) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Project is closed", "El proyecto está cerrado")
        }
        // Validate the task dueDate is between the project date from and date to
        if (Timestamp.from(Instant.parse(newTaskDto.taskDueDate)).before(projectEntity.projectDateFrom) || Timestamp.from(Instant.parse(newTaskDto.taskDueDate)).after(projectEntity.projectDateTo)) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Task dueDate is not between the project date from and date to", "La fecha límite de la tarea no está entre la fecha de inicio y la fecha de finalización del proyecto")
        }
        // Validate the task assignees exist
        if (userRepository.findAllByUserIdInAndStatusIsTrue(newTaskDto.taskAssigneeIds).size != newTaskDto.taskAssigneeIds.size) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: At least one assignee is invalid", "Al menos un miembro asignado es inválido")
        }
        // Validate that the user is the project owner or a project moderator
        if (projectOwnerRepository.findByProjectIdAndUserIdAndStatusIsTrue(newTaskDto.projectId.toLong(), userId) == null && projectModeratorRepository.findByProjectIdAndUserIdAndStatusIsTrue(newTaskDto.projectId.toLong(), userId) == null) {
            throw EtnException(HttpStatus.FORBIDDEN, "Error: User is not the project owner or a project moderator", "El usuario no es el propietario del proyecto o un colaborador del proyecto")
        }
        // Validate the assignees are project members
        if (projectMemberRepository.findAllByProjectIdAndUserIdInAndStatusIsTrue(newTaskDto.projectId.toLong(), newTaskDto.taskAssigneeIds).size != newTaskDto.taskAssigneeIds.size) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: At least one assignee is not a project member", "Al menos un miembro asignado no es un miembro del proyecto")
        }
        // Validate that the task file ids are valid
        if (newTaskDto.taskFileIds.isNotEmpty()) {
            // Validate the task files exist
            if (fileRepository.findAllByFileIdInAndStatusIsTrue(newTaskDto.taskFileIds).size != newTaskDto.taskFileIds.size) {
                throw EtnException(HttpStatus.BAD_REQUEST, "Error: At least one task file is invalid", "Al menos un archivo adjunto es inválido")
            }
        }

        val originalTaskDueDate = taskEntity.taskDueDate
        val originalTaskPriorityId = taskEntity.taskPriorityId
        val originalTaskStatusId = taskEntity.taskStatusId

        val originalAssignees = taskAssigneeRepository.findAllByTaskIdAndStatusIsTrue(taskId).map { it.userId }

        logger.info("Updating the task with id $taskId")
        // Update the task
        taskEntity.taskName = newTaskDto.taskName
        taskEntity.taskDescription = newTaskDto.taskDescription
        taskEntity.taskDueDate = Timestamp.from(Instant.parse(newTaskDto.taskDueDate))
        taskEntity.taskPriorityId = newTaskDto.taskPriorityId
        taskRepository.save(taskEntity)
        logger.info("Task updated with id $taskId")

        // Delete previous task assignees changing their status to false if they are different
        val taskAssigneeEntities = taskAssigneeRepository.findAllByTaskIdAndStatusIsTrue(taskId)
        // If they are different, update the task assignees
        if (taskAssigneeEntities.map { it.userId }.toSet() != newTaskDto.taskAssigneeIds.map { it }.toSet()) {
            taskAssigneeEntities.forEach {
                it.status = false
                taskAssigneeRepository.save(it)
            }
            // Create the new task assignees
            newTaskDto.taskAssigneeIds.forEach { assigneeId ->
                val taskAssigneeEntity = TaskAssignee()
                taskAssigneeEntity.taskId = taskEntity.taskId
                taskAssigneeEntity.userId = assigneeId
                taskAssigneeRepository.save(taskAssigneeEntity)
            }
        }
        logger.info("Task assignees updated for task $taskId")

        // Delete previous task files changing their status to false if they are different
        val taskFileEntities = taskFileRepository.findAllByTaskIdAndStatusIsTrue(taskId)
        // If they are different, update the task files
        if (taskFileEntities.map { it.fileId }.toSet() != newTaskDto.taskFileIds.map { it }.toSet()) {
            taskFileEntities.forEach {
                it.status = false
                taskFileRepository.save(it)
            }
            // Create the new task files
            newTaskDto.taskFileIds.forEach { fileId ->
                val taskFileEntity = TaskFile()
                taskFileEntity.taskId = taskEntity.taskId
                taskFileEntity.fileId = fileId
                taskFileRepository.save(taskFileEntity)
            }
        }
        logger.info("Task files updated for task $taskId")

        // Create the task history
        // If the dueDate has changed
        if (originalTaskDueDate != taskEntity.taskDueDate) {
            val taskHistoryEntity = TaskHistory()
            taskHistoryEntity.taskId = taskEntity.taskId
            taskHistoryEntity.userId = userId.toInt()
            taskHistoryEntity.fieldName = TaskHistoryType.FECHA_LIMITE
            taskHistoryEntity.previousValue = originalTaskDueDate.toString()
            taskHistoryEntity.newValue = taskEntity.taskDueDate.toString()
            taskHistoryRepository.save(taskHistoryEntity)
        }
        // If the priority has changed
        if (originalTaskPriorityId != taskEntity.taskPriorityId) {
            val taskPriorityEntities = taskPriorityRepository.findAllByStatusIsTrueOrderByTaskPriorityId()
            val taskHistoryEntity = TaskHistory()
            taskHistoryEntity.taskId = taskEntity.taskId
            taskHistoryEntity.userId = userId.toInt()
            taskHistoryEntity.fieldName = TaskHistoryType.PRIORIDAD
            taskHistoryEntity.previousValue = taskPriorityEntities.find { it.taskPriorityId == originalTaskPriorityId }!!.taskPriorityName
            taskHistoryEntity.newValue = taskPriorityEntities.find { it.taskPriorityId == taskEntity.taskPriorityId }!!.taskPriorityName
            taskHistoryRepository.save(taskHistoryEntity)
        }
        // If the assignees have changed
        if (originalAssignees.toSet() != newTaskDto.taskAssigneeIds.map { it }.toSet()) {
            val taskHistoryEntity = TaskHistory()
            taskHistoryEntity.taskId = taskEntity.taskId
            taskHistoryEntity.userId = userId.toInt()
            taskHistoryEntity.fieldName = TaskHistoryType.RESPONSABLE
            val assignedNames = userRepository.findAllByUserIdInAndStatusIsTrue(originalAssignees).map { it.firstName + " " + it.lastName }
            taskHistoryEntity.previousValue = assignedNames.joinToString(", ")
            // Get the assignee names
            val assigneeNames = userRepository.findAllByUserIdInAndStatusIsTrue(newTaskDto.taskAssigneeIds).map { it.firstName + " " + it.lastName }
            taskHistoryEntity.newValue = assigneeNames.joinToString(", ")
            taskHistoryRepository.save(taskHistoryEntity)
        }

        // Send the notification to the assignees if the assignees have changed
        if (originalAssignees.toSet() != newTaskDto.taskAssigneeIds.map { it }.toSet()) {
            taskAssigneeEntities.forEach { taskAssigneeEntity ->
                val assigneeEmail = userRepository.findByUserIdAndStatusIsTrue(taskAssigneeEntity.userId.toLong())!!.email
                val assigneeTokens = firebaseTokenRepository.findAllByUserIdAndStatusIsTrue(taskAssigneeEntity.userId.toLong()).map { it.firebaseToken }
                val assigneeMessageTittle = "Tarea reasignada"
                val assigneeMessageBody = "Se le ha reasignado la tarea '${taskEntity.taskName}' en el proyecto '${projectEntity.projectName}'"
                val notificationEntity = Notification()
                notificationEntity.messageTitle = assigneeMessageTittle
                notificationEntity.messageBody = assigneeMessageBody
                notificationEntity.userId = taskAssigneeEntity.userId
                notificationRepository.save(notificationEntity)
                emailService.sendEmail(assigneeEmail, assigneeMessageTittle, assigneeMessageBody)
                assigneeTokens.forEach { token ->
                        firebaseMessagingService.sendNotification(token, assigneeMessageTittle, assigneeMessageBody)
                }
            }
        }
    }

    fun updateTaskStatus(
        taskId: Long,
        taskStatusId: Long
    ) {
        // Get the user id from the token
        val userId = AuthUtil.getUserIdFromAuthToken() ?: throw EtnException(HttpStatus.UNAUTHORIZED, "Error: Unauthorized", "No autorizado")
        // Validate the task exists
        val taskEntity = taskRepository.findByTaskIdAndStatusIsTrue(taskId)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: Task not found", "Tarea no encontrada")
        // Validate the task status exists
        taskStatusRepository.findByTaskStatusIdAndStatusIsTrue(taskStatusId)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: Task status not found", "Estado de la tarea no encontrado")
        // Validate the task status is not the same as the current status
        if (taskEntity.taskStatusId == taskStatusId.toInt()) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Task status is the same as the current status", "El estado de la tarea es el mismo que el estado actual")
        }
        // Validate that the user is the project owner, a project moderator or a project member
        if (projectOwnerRepository.findByProjectIdAndUserIdAndStatusIsTrue(taskEntity.projectId.toLong(), userId) == null && projectModeratorRepository.findByProjectIdAndUserIdAndStatusIsTrue(taskEntity.projectId.toLong(), userId) == null && projectMemberRepository.findByProjectIdAndUserIdAndStatusIsTrue(taskEntity.projectId.toLong(), userId) == null) {
            throw EtnException(HttpStatus.FORBIDDEN, "Error: User is not the project owner or a project moderator", "El usuario no es el propietario del proyecto o un colaborador del proyecto")
        }

        val originalTaskStatusId = taskEntity.taskStatusId
        // Update the task status
        if (taskStatusId.toInt() == 3) {
            taskEntity.taskEndDate = Timestamp.from(Instant.now())
        }
        taskEntity.taskStatusId = taskStatusId.toInt()
        taskRepository.save(taskEntity)

        // Create the task history
        val taskStatusEntities = taskStatusRepository.findAllByStatusIsTrueOrderByTaskStatusId()
        val taskHistoryEntity = TaskHistory()
        taskHistoryEntity.taskId = taskEntity.taskId
        taskHistoryEntity.userId = userId.toInt()
        taskHistoryEntity.fieldName = TaskHistoryType.ESTADO
        taskHistoryEntity.previousValue = taskStatusEntities.find { it.taskStatusId == originalTaskStatusId }!!.taskStatusName
        taskHistoryEntity.newValue = taskStatusEntities.find { it.taskStatusId == taskEntity.taskStatusId }!!.taskStatusName
        taskHistoryRepository.save(taskHistoryEntity)

        // Notify the project owner and moderators if the task status is completed
        if (taskStatusId.toInt() == 3) {
            val projectOwnerEntities = projectOwnerRepository.findAllByProjectIdAndStatusIsTrue(taskEntity.projectId.toLong())
            val projectModeratorEntities = projectModeratorRepository.findAllByProjectIdAndStatusIsTrue(taskEntity.projectId.toLong())
            projectOwnerEntities.forEach { projectOwnerEntity ->
                val projectName = projectRepository.findByProjectIdAndStatusIsTrue(taskEntity.projectId.toLong())!!.projectName
                val ownerEmail = userRepository.findByUserIdAndStatusIsTrue(projectOwnerEntity.userId.toLong())!!.email
                logger.info("Sending notification to project owner $ownerEmail")
                val ownerTokens = firebaseTokenRepository.findAllByUserIdAndStatusIsTrue(projectOwnerEntity.userId.toLong()).map { it.firebaseToken }
                val ownerMessageTittle = "Tarea completada"
                val ownerMessageBody = "La tarea '${taskEntity.taskName}' del proyecto '$projectName' ha sido completada"
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
                val projectName = projectRepository.findByProjectIdAndStatusIsTrue(taskEntity.projectId.toLong())!!.projectName
                val moderatorEmail = userRepository.findByUserIdAndStatusIsTrue(projectModeratorEntity.userId.toLong())!!.email
                logger.info("Sending notification to project moderator $moderatorEmail")
                val moderatorTokens = firebaseTokenRepository.findAllByUserIdAndStatusIsTrue(projectModeratorEntity.userId.toLong()).map { it.firebaseToken }
                val moderatorMessageTittle = "Tarea completada"
                val moderatorMessageBody = "La tarea ${taskEntity.taskName} en del proyecto '$projectName' ha sido completada"
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
    }

    fun deleteTask(taskId: Long) {
        // Get the project owner id from the token
        val userId = AuthUtil.getUserIdFromAuthToken() ?: throw EtnException(HttpStatus.UNAUTHORIZED, "Error: Unauthorized", "No autorizado")
        // Validate the task exists
        val taskEntity = taskRepository.findByTaskIdAndStatusIsTrue(taskId)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: Task not found", "Tarea no encontrada")
        // Validate that the task status id is equal to 1 (To Do) or (Pendiente)
        if (taskEntity.taskStatusId != 1) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Task status is in progress or completed", "La tarea ya está en progreso o completada")
        }
        // Validate that the user is the project owner or a project moderator
        if (projectOwnerRepository.findByProjectIdAndUserIdAndStatusIsTrue(taskEntity.projectId.toLong(), userId) == null && projectModeratorRepository.findByProjectIdAndUserIdAndStatusIsTrue(taskEntity.projectId.toLong(), userId) == null) {
            throw EtnException(HttpStatus.FORBIDDEN, "Error: User is not the project owner or a project moderator", "El usuario no es el propietario del proyecto o un colaborador del proyecto")
        }
        // Delete the task
        taskEntity.status = false
        taskRepository.save(taskEntity)
    }

    fun getTaskHistory(taskId: Long): List<TaskHistoryDto> {
        // Get the user id from the token
        val userId = AuthUtil.getUserIdFromAuthToken() ?: throw EtnException(HttpStatus.UNAUTHORIZED, "Error: Unauthorized", "No autorizado")
        // Validate the task exists
        val taskEntity = taskRepository.findByTaskIdAndStatusIsTrue(taskId)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: Task not found", "Tarea no encontrada")
        // Validate that the user is the project owner, a project moderator or a project member
        if (projectOwnerRepository.findByProjectIdAndUserIdAndStatusIsTrue(taskEntity.projectId.toLong(), userId) == null && projectModeratorRepository.findByProjectIdAndUserIdAndStatusIsTrue(taskEntity.projectId.toLong(), userId) == null && projectMemberRepository.findByProjectIdAndUserIdAndStatusIsTrue(taskEntity.projectId.toLong(), userId) == null) {
            throw EtnException(HttpStatus.FORBIDDEN, "Error: User is not the project owner or a project moderator", "El usuario no es el propietario del proyecto o un colaborador del proyecto")
        }
        // Get the task history
        val taskHistoryEntities = taskHistoryRepository.findAllByTaskIdAndStatusIsTrueOrderByTaskHistoryIdDesc(taskId)
        return taskHistoryEntities.map { TaskHistoryMapper.entityToDto(it) }
    }

    fun createTaskFeedback(
        taskId: Long,
        newTaskFeedbackDto: NewTaskFeedbackDto
    ) {
        // Validate the rating is between 1 and 5
        if (newTaskFeedbackDto.taskRating < 1 || newTaskFeedbackDto.taskRating > 5) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Rating must be between 1 and 5", "La calificación debe estar entre 1 y 5")
        }
        // Get the user id from the token
        val userId = AuthUtil.getUserIdFromAuthToken() ?: throw EtnException(HttpStatus.UNAUTHORIZED, "Error: Unauthorized", "No autorizado")
        // Validate the task exists
        val taskEntity = taskRepository.findByTaskIdAndStatusIsTrue(taskId)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: Task not found", "Tarea no encontrada")
        // Validate that the user is the project owner, a project moderator or a project member
        if (projectOwnerRepository.findByProjectIdAndUserIdAndStatusIsTrue(taskEntity.projectId.toLong(), userId) == null && projectModeratorRepository.findByProjectIdAndUserIdAndStatusIsTrue(taskEntity.projectId.toLong(), userId) == null && projectMemberRepository.findByProjectIdAndUserIdAndStatusIsTrue(taskEntity.projectId.toLong(), userId) == null) {
            throw EtnException(HttpStatus.FORBIDDEN, "Error: User is not the project owner or a project moderator", "El usuario no es el propietario del proyecto o un colaborador del proyecto")
        }
        // Validate the task status is 3
        if (taskEntity.taskStatusId != 3) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Task status is not completed", "La tarea no está completada")
        }
        // Validate the task rating and comment are not empty
        if (taskEntity.taskRatingComment.isNotEmpty() && taskEntity.taskRating != 0) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Task feedback already exists", "La retroalimentación de la tarea ya existe")
        }
        // Add the rating and feedback to the task
        taskEntity.taskEndDate = Timestamp.from(Instant.now())
        taskEntity.taskRating = newTaskFeedbackDto.taskRating
        taskEntity.taskRatingComment = newTaskFeedbackDto.taskRatingComment
        taskRepository.save(taskEntity)
    }

    @Scheduled(cron = "0 0 21 * * *")
    fun sendNotification() {
        val secondsToAdd: Long = 60 * 60 * 24 // 24 hours
        val taskEntities = taskRepository.findAllByTaskDueDateBetweenAndTaskEndDateIsNullAndStatusIsTrueOrderByTaskDueDate(Timestamp.from(Instant.now()), Timestamp.from(Instant.now().plusSeconds(secondsToAdd)))

        val taskAssigneeEntities = taskAssigneeRepository.findAllByTaskIdInAndStatusIsTrue(taskEntities.map { it.taskId })

        taskAssigneeEntities.forEach { taskAssigneeEntity ->
            val projectName = projectRepository.findByProjectIdAndStatusIsTrue(taskAssigneeEntity.task!!.projectId.toLong())!!.projectName
            val assigneeEmail = userRepository.findByUserIdAndStatusIsTrue(taskAssigneeEntity.userId.toLong())!!.email
            logger.info("Sending notification to project assignee $assigneeEmail")
            val assigneeTokens = firebaseTokenRepository.findAllByUserIdAndStatusIsTrue(taskAssigneeEntity.userId.toLong()).map { it.firebaseToken }
            val assigneeMessageTittle = "Recordatorio: Tarea por finalizar"
            val assigneeMessageBody = "La tarea: '${taskAssigneeEntity.task!!.taskName}' en el proyecto: '$projectName' está por finalizar en las próximas 24 horas. Por favor, complete la tarea."
            val notificationEntity = Notification()
            notificationEntity.messageTitle = assigneeMessageTittle
            notificationEntity.messageBody = assigneeMessageBody
            notificationEntity.userId = taskAssigneeEntity.userId
            notificationRepository.save(notificationEntity)
            emailService.sendEmail(assigneeEmail, assigneeMessageTittle, assigneeMessageBody)
            assigneeTokens.forEach { token ->
                firebaseMessagingService.sendNotification(token, assigneeMessageTittle, assigneeMessageBody)
            }
        }

    }
}
