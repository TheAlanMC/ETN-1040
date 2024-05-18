package bo.edu.umsa.backend.service

import bo.edu.umsa.backend.dto.*
import bo.edu.umsa.backend.entity.Task
import bo.edu.umsa.backend.entity.TaskAssignee
import bo.edu.umsa.backend.entity.TaskFile
import bo.edu.umsa.backend.exception.EtnException
import bo.edu.umsa.backend.mapper.TaskCommentMapper
import bo.edu.umsa.backend.mapper.TaskMapper
import bo.edu.umsa.backend.mapper.TaskPartialMapper
import bo.edu.umsa.backend.mapper.TaskStatusMapper
import bo.edu.umsa.backend.repository.*
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
class TaskService @Autowired constructor(private val fileRepository: FileRepository, private val projectRepository: ProjectRepository, private val projectOwnerRepository: ProjectOwnerRepository, private val projectModeratorRepository: ProjectModeratorRepository, private val projectMemberRepository: ProjectMemberRepository, private val userRepository: UserRepository, private val taskRepository: TaskRepository, private val taskAssigneeRepository: TaskAssigneeRepository, private val taskFileRepository: TaskFileRepository, private val taskStatusRepository: TaskStatusRepository, private val taskCommentRepository: TaskCommentRepository) {
    companion object {
        private val logger = org.slf4j.LoggerFactory.getLogger(TaskService::class.java)
    }

    fun getAllStatuses(): List<TaskStatusDto> {
        logger.info("Getting all task statuses")
        val statuses = taskStatusRepository.findAllByStatusIsTrueOrderByTaskStatusId()
        return statuses.map { TaskStatusMapper.entityToDto(it) }
    }

    fun getTasks(sortBy: String, sortType: String, page: Int, size: Int, keyword: String?, statuses: List<String>?, dateFrom: String?, dateTo: String?): Page<TaskPartialDto> {
        val userId = AuthUtil.getUserIdFromAuthToken() ?: throw EtnException(HttpStatus.UNAUTHORIZED, "Error: Unauthorized", "No autorizado")
        logger.info("Getting the tasks for user $userId")
        // Validate the user exists
        userRepository.findByUserIdAndStatusIsTrue(userId)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: User not found", "Usuario no encontrado")

        // Pagination and sorting
        val pageable: Pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortType), sortBy))
        var specification: Specification<Task> = Specification.where(null)
        specification = specification.and(specification.and(TaskSpecification.statusIsTrue()))
        specification = specification.and(specification.and(TaskSpecification.taskAssignee(userId.toInt())))

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
        // Validate the name, description, and deadline are not empty
        if (newTaskDto.taskName.isEmpty() || newTaskDto.taskDescription.isEmpty() || newTaskDto.taskDeadline.isEmpty()) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: At least one required field is blank", "Al menos un campo requerido está en blanco")
        }
        // Validate the dates have the correct format
        try {
            val deadline = Timestamp.from(Instant.parse(newTaskDto.taskDeadline))
            logger.info("Deadline: $deadline")
        } catch (e: Exception) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Date format is incorrect", "El formato de fecha es incorrecto")
        }
        if (Timestamp.from(Instant.parse(newTaskDto.taskDeadline)).before(Timestamp.from(Instant.now()))) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Deadline is in the past", "La fecha límite está en el pasado")
        }
        // Validate the task priority is between 1 and 10
        if (newTaskDto.taskPriority < 1 || newTaskDto.taskPriority > 10) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Task priority must be between 1 and 10", "La prioridad de la tarea debe estar entre 1 y 10")
        }
        // Validate the task assignees are not empty
        if (newTaskDto.taskAssigneeIds.isEmpty()) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: At least one assignee is required", "Se requiere al menos un miembro asignado")
        }
        // Get the project owner id from the token
        val userId = AuthUtil.getUserIdFromAuthToken() ?: throw EtnException(HttpStatus.UNAUTHORIZED, "Error: Unauthorized", "No autorizado")
        // Validate the project exists
        val projectEntity = projectRepository.findByProjectIdAndStatusIsTrue(newTaskDto.projectId.toLong())
            ?: throw EtnException(HttpStatus.BAD_REQUEST, "Error: Project does not exist", "El proyecto no existe")
        // Validate the task deadline is between the project date from and date to
        if (Timestamp.from(Instant.parse(newTaskDto.taskDeadline)).before(projectEntity.dateFrom) || Timestamp.from(Instant.parse(newTaskDto.taskDeadline)).after(projectEntity.dateTo)) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Task deadline is not between the project date from and date to", "La fecha límite de la tarea no está entre la fecha de inicio y la fecha de finalización del proyecto")
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

        logger.info("Creating a new task with owner id $userId")
        // Create the task
        val taskEntity = Task()
        taskEntity.projectId = newTaskDto.projectId
        taskEntity.taskStatusId = 1 // Default status is "To Do" or "Pendiente"
        taskEntity.taskName = newTaskDto.taskName
        taskEntity.taskDescription = newTaskDto.taskDescription
        taskEntity.taskDeadline = Timestamp.from(Instant.parse(newTaskDto.taskDeadline))
        taskEntity.taskPriority = newTaskDto.taskPriority
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
    }

    fun updateTask(taskId: Long, newTaskDto: NewTaskDto) {
        // Validate the name, description, and deadline are not empty
        if (newTaskDto.taskName.isEmpty() || newTaskDto.taskDescription.isEmpty() || newTaskDto.taskDeadline.isEmpty()) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: At least one required field is blank", "Al menos un campo requerido está en blanco")
        }
        // Validate the dates have the correct format
        try {
            val deadline = Timestamp.from(Instant.parse(newTaskDto.taskDeadline))
            logger.info("Deadline: $deadline")
        } catch (e: Exception) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Date format is incorrect", "El formato de fecha es incorrecto")
        }
        if (Timestamp.from(Instant.parse(newTaskDto.taskDeadline)).before(Timestamp.from(Instant.now()))) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Deadline is in the past", "La fecha límite está en el pasado")
        }
        // Validate the task priority is between 1 and 10
        if (newTaskDto.taskPriority < 1 || newTaskDto.taskPriority > 10) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Task priority must be between 1 and 10", "La prioridad de la tarea debe estar entre 1 y 10")
        }
        // Validate the task assignees are not empty
        if (newTaskDto.taskAssigneeIds.isEmpty()) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: At least one assignee is required", "Se requiere al menos un miembro asignado")
        }
        // Get the project owner id from the token
        val userId = AuthUtil.getUserIdFromAuthToken() ?: throw EtnException(HttpStatus.UNAUTHORIZED, "Error: Unauthorized", "No autorizado")
        // Validate the task exists
        val taskEntity = taskRepository.findByTaskIdAndStatusIsTrue(taskId)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: Task not found", "Tarea no encontrada")
        // Validate the project exists
        val projectEntity = projectRepository.findByProjectIdAndStatusIsTrue(taskEntity.projectId.toLong())
            ?: throw EtnException(HttpStatus.BAD_REQUEST, "Error: Project does not exist", "El proyecto no existe")
        // Validate the task deadline is between the project date from and date to
        if (Timestamp.from(Instant.parse(newTaskDto.taskDeadline)).before(projectEntity.dateFrom) || Timestamp.from(Instant.parse(newTaskDto.taskDeadline)).after(projectEntity.dateTo)) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Task deadline is not between the project date from and date to", "La fecha límite de la tarea no está entre la fecha de inicio y la fecha de finalización del proyecto")
        }

        // Validate the task assignees exist
        if (userRepository.findAllByUserIdInAndStatusIsTrue(newTaskDto.taskAssigneeIds).size != newTaskDto.taskAssigneeIds.size) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: At least one assignee is invalid", "Al menos un miembro asignado es inválido")
        }
        // Validate that the user is the project owner or a project moderator
        if (projectOwnerRepository.findByProjectIdAndUserIdAndStatusIsTrue(taskEntity.projectId.toLong(), userId) == null && projectModeratorRepository.findByProjectIdAndUserIdAndStatusIsTrue(taskEntity.projectId.toLong(), userId) == null) {
            throw EtnException(HttpStatus.FORBIDDEN, "Error: User is not the project owner or a project moderator", "El usuario no es el propietario del proyecto o un colaborador del proyecto")
        }
        // Validate the assignees are project members
        if (projectMemberRepository.findAllByProjectIdAndUserIdInAndStatusIsTrue(taskEntity.projectId.toLong(), newTaskDto.taskAssigneeIds).size != newTaskDto.taskAssigneeIds.size) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: At least one assignee is not a project member", "Al menos un miembro asignado no es un miembro del proyecto")
        }
        // Validate that the task file ids are valid
        if (newTaskDto.taskFileIds.isNotEmpty()) {
            // Validate the task files exist
            if (fileRepository.findAllByFileIdInAndStatusIsTrue(newTaskDto.taskFileIds).size != newTaskDto.taskFileIds.size) {
                throw EtnException(HttpStatus.BAD_REQUEST, "Error: At least one task file is invalid", "Al menos un archivo adjunto es inválido")
            }
        }

        logger.info("Updating the task with id $taskId")
        // Update the task
        taskEntity.taskName = newTaskDto.taskName
        taskEntity.taskDescription = newTaskDto.taskDescription
        taskEntity.taskDeadline = Timestamp.from(Instant.parse(newTaskDto.taskDeadline))
        taskEntity.taskPriority = newTaskDto.taskPriority
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
    }

    fun updateTaskStatus(taskId: Long, taskStatusId: Long) {
        // Get the user id from the token
        val userId = AuthUtil.getUserIdFromAuthToken() ?: throw EtnException(HttpStatus.UNAUTHORIZED, "Error: Unauthorized", "No autorizado")
        // Validate the task exists
        val taskEntity = taskRepository.findByTaskIdAndStatusIsTrue(taskId)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: Task not found", "Tarea no encontrada")
        // Validate the task status exists
        taskStatusRepository.findByTaskStatusIdAndStatusIsTrue(taskStatusId)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: Task status not found", "Estado de la tarea no encontrado")
        // Validate that the user is the project owner, a project moderator or a project member
        if (projectOwnerRepository.findByProjectIdAndUserIdAndStatusIsTrue(taskEntity.projectId.toLong(), userId) == null && projectModeratorRepository.findByProjectIdAndUserIdAndStatusIsTrue(taskEntity.projectId.toLong(), userId) == null && projectMemberRepository.findByProjectIdAndUserIdAndStatusIsTrue(taskEntity.projectId.toLong(), userId) == null) {
            throw EtnException(HttpStatus.FORBIDDEN, "Error: User is not the project owner or a project moderator", "El usuario no es el propietario del proyecto o un colaborador del proyecto")
        }
        // Update the task status
        taskEntity.taskStatusId = taskStatusId.toInt()
        taskRepository.save(taskEntity)
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

    fun getTaskComments(taskId: Long): List<TaskCommentDto> {
        val userId = AuthUtil.getUserIdFromAuthToken() ?: throw EtnException(HttpStatus.UNAUTHORIZED, "Error: Unauthorized", "No autorizado")
        logger.info("Getting the task comments for user $userId")
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
        // Get the task comments
        val taskCommentEntities = taskCommentRepository.findAllByTaskIdAndStatusIsTrueOrderByCommentNumberDesc(taskId)
        return taskCommentEntities.map { TaskCommentMapper.entityToDto(it) }
    }
}
