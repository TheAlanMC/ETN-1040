package bo.edu.umsa.backend.service

import bo.edu.umsa.backend.dto.NewTaskCommentDto
import bo.edu.umsa.backend.dto.TaskCommentDto
import bo.edu.umsa.backend.entity.TaskComment
import bo.edu.umsa.backend.entity.TaskCommentFile
import bo.edu.umsa.backend.exception.EtnException
import bo.edu.umsa.backend.mapper.TaskCommentMapper
import bo.edu.umsa.backend.repository.*
import bo.edu.umsa.backend.util.AuthUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class TaskCommentService @Autowired constructor(
    private val fileRepository: FileRepository,
    private val projectOwnerRepository: ProjectOwnerRepository,
    private val projectModeratorRepository: ProjectModeratorRepository,
    private val projectMemberRepository: ProjectMemberRepository,
    private val userRepository: UserRepository,
    private val taskRepository: TaskRepository,
    private val taskCommentRepository: TaskCommentRepository,
    private val taskCommentFileRepository: TaskCommentFileRepository,
) {
    companion object {
        private val logger = org.slf4j.LoggerFactory.getLogger(TaskCommentService::class.java)
    }

    fun getCommentById(commentId: Long): TaskCommentDto {
        val userId = AuthUtil.getUserIdFromAuthToken() ?: throw EtnException(HttpStatus.UNAUTHORIZED, "Error: Unauthorized", "No autorizado")
        logger.info("Getting the comment with id $commentId for user $userId")
        // Validate the user exists
        userRepository.findByUserIdAndStatusIsTrue(userId)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: User not found", "Usuario no encontrado")
        // Validate the comment exists
        val taskCommentEntity = taskCommentRepository.findByTaskCommentIdAndStatusIsTrue(commentId)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: Comment not found", "Comentario no encontrado")
        // Validate that the user is the user who created the comment
        if (taskCommentEntity.userId.toLong() != userId) {
            throw EtnException(HttpStatus.FORBIDDEN, "Error: User is not the comment owner", "El usuario no es el propietario del comentario")
        }
        return TaskCommentMapper.entityToDto(taskCommentEntity)
    }

    fun createComment(newTaskCommentDto: NewTaskCommentDto) {
        // Validate the comment is not empty
        if (newTaskCommentDto.taskComment.isEmpty()) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Comment is blank", "El comentario está en blanco")
        }
        // Get the user id from the token
        val userId = AuthUtil.getUserIdFromAuthToken() ?: throw EtnException(HttpStatus.UNAUTHORIZED, "Error: Unauthorized", "No autorizado")
        // Validate the task exists
        val taskEntity = taskRepository.findByTaskIdAndStatusIsTrue(newTaskCommentDto.taskId.toLong())
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: Task not found", "Tarea no encontrada")
        // Validate that the user is the project owner, a project moderator or a project member
        if (projectOwnerRepository.findByProjectIdAndUserIdAndStatusIsTrue(taskEntity.projectId.toLong(), userId) == null && projectModeratorRepository.findByProjectIdAndUserIdAndStatusIsTrue(taskEntity.projectId.toLong(), userId) == null && projectMemberRepository.findByProjectIdAndUserIdAndStatusIsTrue(taskEntity.projectId.toLong(), userId) == null) {
            throw EtnException(HttpStatus.FORBIDDEN, "Error: User is not the project owner or a project moderator", "El usuario no es el propietario del proyecto, un colaborador del proyecto o un miembro del proyecto")
        }
        logger.info("Creating a new comment with owner id $userId")
        // Validate that the task file ids are valid
        if (newTaskCommentDto.taskCommentFileIds.isNotEmpty()) {
            // Validate the task files exist
            if (fileRepository.findAllByFileIdInAndStatusIsTrue(newTaskCommentDto.taskCommentFileIds).size != newTaskCommentDto.taskCommentFileIds.size) {
                throw EtnException(HttpStatus.BAD_REQUEST, "Error: At least one task file is invalid", "Al menos un archivo adjunto es inválido")
            }
        }

        // Get the comment number
        val taskCommentNumber = taskCommentRepository.findFirstByTaskIdOrderByTaskCommentNumberDesc(newTaskCommentDto.taskId.toLong())?.taskCommentNumber?.plus(1)
            ?: 1

        // Create the comment
        val taskCommentEntity = TaskComment()
        taskCommentEntity.taskId = newTaskCommentDto.taskId
        taskCommentEntity.userId = userId.toInt()
        taskCommentEntity.taskCommentNumber = taskCommentNumber
        taskCommentEntity.taskComment = newTaskCommentDto.taskComment
        taskCommentRepository.save(taskCommentEntity)
        logger.info("Comment created with id ${taskCommentEntity.taskCommentId}")

        // Create the task comment files
        newTaskCommentDto.taskCommentFileIds.forEach { fileId ->
            val taskCommentFileEntity = TaskCommentFile()
            taskCommentFileEntity.taskCommentId = taskCommentEntity.taskCommentId
            taskCommentFileEntity.fileId = fileId
            taskCommentFileRepository.save(taskCommentFileEntity)
        }
        logger.info("Task comment files created for comment ${taskCommentEntity.taskCommentId}")
    }

    fun updateComment(
        commentId: Long,
        newTaskCommentDto: NewTaskCommentDto
    ) {
        // Validate the comment is not empty
        if (newTaskCommentDto.taskComment.isEmpty()) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Comment is blank", "El comentario está en blanco")
        }
        // Get the user id from the token
        val userId = AuthUtil.getUserIdFromAuthToken() ?: throw EtnException(HttpStatus.UNAUTHORIZED, "Error: Unauthorized", "No autorizado")
        // Validate the comment exists
        val taskCommentEntity = taskCommentRepository.findByTaskCommentIdAndStatusIsTrue(commentId)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: Comment not found", "Comentario no encontrado")
        // Validate that the user is the user who created the comment
        if (taskCommentEntity.userId.toLong() != userId) {
            throw EtnException(HttpStatus.FORBIDDEN, "Error: User is not the comment owner", "El usuario no es el propietario del comentario")
        }
        // Validate that the task file ids are valid
        if (newTaskCommentDto.taskCommentFileIds.isNotEmpty()) {
            // Validate the task files exist
            if (fileRepository.findAllByFileIdInAndStatusIsTrue(newTaskCommentDto.taskCommentFileIds).size != newTaskCommentDto.taskCommentFileIds.size) {
                throw EtnException(HttpStatus.BAD_REQUEST, "Error: At least one task file is invalid", "Al menos un archivo adjunto es inválido")
            }
        }
        logger.info("Updating the comment with id $commentId")
        // Update the comment
        taskCommentEntity.taskComment = newTaskCommentDto.taskComment
        taskCommentRepository.save(taskCommentEntity)
        logger.info("Comment updated with id $commentId")

        // Delete previous task comment files changing their status to false if they are different
        val taskCommentFileEntities = taskCommentFileRepository.findAllByTaskCommentIdAndStatusIsTrue(commentId)
        // If they are different, update the task comment files
        if (taskCommentFileEntities.map { it.fileId }.toSet() != newTaskCommentDto.taskCommentFileIds.map { it }.toSet()) {
            taskCommentFileEntities.forEach {
                it.status = false
                taskCommentFileRepository.save(it)
            }
            // Create the new task comment files
            newTaskCommentDto.taskCommentFileIds.forEach { fileId ->
                val taskCommentFileEntity = TaskCommentFile()
                taskCommentFileEntity.taskCommentId = taskCommentEntity.taskCommentId
                taskCommentFileEntity.fileId = fileId
                taskCommentFileRepository.save(taskCommentFileEntity)
            }
        }
        logger.info("Task comment files updated for comment $commentId")
    }

    fun deleteComment(commentId: Long) {
        // Get the user id from the token
        val userId = AuthUtil.getUserIdFromAuthToken() ?: throw EtnException(HttpStatus.UNAUTHORIZED, "Error: Unauthorized", "No autorizado")
        // Validate the comment exists
        val taskCommentEntity = taskCommentRepository.findByTaskCommentIdAndStatusIsTrue(commentId)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: Comment not found", "Comentario no encontrado")
        // Validate that the user is the user who created the comment
        if (taskCommentEntity.userId.toLong() != userId) {
            throw EtnException(HttpStatus.FORBIDDEN, "Error: User is not the comment owner", "El usuario no es el propietario del comentario")
        }
        // Delete the comment
        taskCommentEntity.status = false
        taskCommentRepository.save(taskCommentEntity)
    }

}
