package bo.edu.umsa.backend.service

import bo.edu.umsa.backend.dto.FileDto
import bo.edu.umsa.backend.dto.NewTaskDto
import bo.edu.umsa.backend.entity.Task
import bo.edu.umsa.backend.entity.TaskAssignee
import bo.edu.umsa.backend.entity.TaskFile
import bo.edu.umsa.backend.entity.User
import bo.edu.umsa.backend.exception.EtnException
import bo.edu.umsa.backend.repository.*
import bo.edu.umsa.backend.service.UserService.Companion
import bo.edu.umsa.backend.util.AuthUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.sql.Timestamp
import java.time.Instant

@Service
class TaskService @Autowired constructor(
    private val fileService: FileService,
    private val fileRepository: FileRepository,
    private val projectRepository: ProjectRepository,
    private val projectOwnerRepository: ProjectOwnerRepository,
    private val projectModeratorRepository: ProjectModeratorRepository,
    private val projectMemberRepository: ProjectMemberRepository,
    private val userRepository: UserRepository,
    private val taskRepository: TaskRepository,
    private val taskAssigneeRepository: TaskAssigneeRepository,
    private val taskFileRepository: TaskFileRepository,
    private val taskStatusRepository: TaskStatusRepository,
) {
    companion object {
        private val logger = org.slf4j.LoggerFactory.getLogger(TaskService::class.java)
    }

    fun createTask(newTaskDto: NewTaskDto) {
        // Validate the name, description, and deadline are not empty
        if (newTaskDto.taskName.isEmpty() || newTaskDto.taskDescription.isEmpty() || newTaskDto.taskDeadline.isEmpty()) {
            throw EtnException(
                HttpStatus.BAD_REQUEST,
                "Error: At least one required field is blank",
                "Al menos un campo requerido está en blanco"
            )
        }
        // Validate the dates have the correct format
        try {
            val deadline = Timestamp.from(Instant.parse(newTaskDto.taskDeadline))
            logger.info("Deadline: $deadline")
        } catch (e: Exception) {
            throw EtnException(
                HttpStatus.BAD_REQUEST, "Error: Date format is incorrect", "El formato de fecha es incorrecto"
            )
        }
        if (Timestamp.from(Instant.parse(newTaskDto.taskDeadline)).before(Timestamp.from(Instant.now()))) {
            throw EtnException(
                HttpStatus.BAD_REQUEST, "Error: Deadline is in the past", "La fecha límite está en el pasado"
            )
        }
        // Validate the task priority is between 1 and 10
        if (newTaskDto.taskPriority < 1 || newTaskDto.taskPriority > 10) {
            throw EtnException(
                HttpStatus.BAD_REQUEST,
                "Error: Task priority must be between 1 and 10",
                "La prioridad de la tarea debe estar entre 1 y 10"
            )
        }
        // Validate the task assignees are not empty
        if (newTaskDto.taskAssigneeIds.isEmpty()) {
            throw EtnException(
                HttpStatus.BAD_REQUEST,
                "Error: At least one assignee is required",
                "Se requiere al menos un miembro asignado"
            )
        }
        // Get the project owner id from the token
        val userId = AuthUtil.getUserIdFromAuthToken() ?: throw EtnException(
            HttpStatus.UNAUTHORIZED, "Error: Unauthorized", "No autorizado"
        )
        // Validate the project exists
        projectRepository.findByProjectIdAndStatusIsTrue(newTaskDto.projectId.toLong()) ?: throw EtnException(
            HttpStatus.BAD_REQUEST, "Error: Project does not exist", "El proyecto no existe"
        )
        // Validate the task assignees exist
        if (userRepository.findAllByUserIdInAndStatusIsTrue(newTaskDto.taskAssigneeIds).size != newTaskDto.taskAssigneeIds.size) {
            throw EtnException(
                HttpStatus.BAD_REQUEST,
                "Error: At least one assignee is invalid",
                "Al menos un miembro asignado es inválido"
            )
        }
        // Validate that the user is the project owner or a project moderator
        if (projectOwnerRepository.findByProjectIdAndUserIdAndStatusIsTrue(
                newTaskDto.projectId.toLong(), userId
            ) == null && projectModeratorRepository.findByProjectIdAndUserIdAndStatusIsTrue(
                newTaskDto.projectId.toLong(), userId
            ) == null
        ) {
            throw EtnException(
                HttpStatus.FORBIDDEN,
                "Error: User is not the project owner or a project moderator",
                "El usuario no es el propietario del proyecto o un colaborador del proyecto"
            )
        }
        // Validate the assignees exist
        if (userRepository.findAllByUserIdInAndStatusIsTrue(newTaskDto.taskAssigneeIds).size != newTaskDto.taskAssigneeIds.size) {
            throw EtnException(
                HttpStatus.BAD_REQUEST,
                "Error: At least one assignee is invalid",
                "Al menos un miembro asignado es inválido"
            )
        }
        // Validate the assignees are project members
        if (projectMemberRepository.findAllByProjectIdAndUserIdInAndStatusIsTrue(
                newTaskDto.projectId.toLong(),
                newTaskDto.taskAssigneeIds).size != newTaskDto.taskAssigneeIds.size
        ) {
            throw EtnException(
                HttpStatus.BAD_REQUEST,
                "Error: At least one assignee is not a project member",
                "Al menos un miembro asignado no es un miembro del proyecto"
            )
        }
        // Validate that the task file ids are valid
        if (newTaskDto.taskFileIds.isNotEmpty()) {
            // Validate the task files exist
            if (fileRepository.findAllByFileIdInAndStatusIsTrue(newTaskDto.taskFileIds).size != newTaskDto.taskFileIds.size) {
                throw EtnException(
                    HttpStatus.BAD_REQUEST,
                    "Error: At least one task file is invalid",
                    "Al menos un archivo adjunto es inválido"
                )
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
}