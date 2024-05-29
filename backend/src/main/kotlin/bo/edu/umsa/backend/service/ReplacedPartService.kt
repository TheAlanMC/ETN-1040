package bo.edu.umsa.backend.service

import bo.edu.umsa.backend.dto.NewReplacedPartDto
import bo.edu.umsa.backend.dto.ReplacedPartDto
import bo.edu.umsa.backend.entity.ReplacedPart
import bo.edu.umsa.backend.entity.ReplacedPartFile
import bo.edu.umsa.backend.exception.EtnException
import bo.edu.umsa.backend.mapper.ReplacedPartMapper
import bo.edu.umsa.backend.repository.*
import bo.edu.umsa.backend.util.AuthUtil
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class ReplacedPartService @Autowired constructor(
    private val fileRepository: FileRepository,
    private val projectOwnerRepository: ProjectOwnerRepository,
    private val projectModeratorRepository: ProjectModeratorRepository,
    private val projectMemberRepository: ProjectMemberRepository,
    private val userRepository: UserRepository,
    private val taskRepository: TaskRepository,
    private val replacedPartRepository: ReplacedPartRepository,
    private val replacedPartFileRepository: ReplacedPartFileRepository
) {
    companion object {
        private val logger = LoggerFactory.getLogger(ReplacedPartService::class.java)
    }

    fun getReplacedPartById(replacedPartId: Long): ReplacedPartDto {
        val userId = AuthUtil.getUserIdFromAuthToken() ?: throw EtnException(HttpStatus.UNAUTHORIZED, "Error: Unauthorized", "No autorizado")
        logger.info("Getting the replaced part with id $replacedPartId for user $userId")
        // Validate the user exists
        userRepository.findByUserIdAndStatusIsTrue(userId)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: User not found", "Usuario no encontrado")
        // Validate the replaced part exists
        val replacedPartEntity = replacedPartRepository.findByReplacedPartIdAndStatusIsTrue(replacedPartId)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: Replaced part not found", "Reemplazo no encontrado")
        return ReplacedPartMapper.entityToDto(replacedPartEntity)
    }

    fun createReplacedPart(newReplacedPartDto: NewReplacedPartDto) {
        // Validate the replaced part is not empty
        if (newReplacedPartDto.replacedPartDescription.trim().isEmpty()) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Replaced part is blank", "La descripción del reemplazo está en blanco")
        }
        // Get the user id from the token
        val userId = AuthUtil.getUserIdFromAuthToken() ?: throw EtnException(HttpStatus.UNAUTHORIZED, "Error: Unauthorized", "No autorizado")
        // Validate the task exists
        val taskEntity = taskRepository.findByTaskIdAndStatusIsTrue(newReplacedPartDto.taskId.toLong())
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: Task not found", "Tarea no encontrada")
        // Validate that the user is the project owner, a project moderator or a project member
        if (projectOwnerRepository.findByProjectIdAndUserIdAndStatusIsTrue(taskEntity.projectId.toLong(), userId) == null && projectModeratorRepository.findByProjectIdAndUserIdAndStatusIsTrue(taskEntity.projectId.toLong(), userId) == null && projectMemberRepository.findByProjectIdAndUserIdAndStatusIsTrue(taskEntity.projectId.toLong(), userId) == null) {
            throw EtnException(HttpStatus.FORBIDDEN, "Error: User is not the project owner or a project moderator", "El usuario no es el propietario del proyecto, un colaborador del proyecto o un miembro del proyecto")
        }
        logger.info("Creating a new replaced part with owner id $userId")
        // Validate that the task file ids are valid
        if (newReplacedPartDto.replacedPartFileIds.isNotEmpty()) {
            // Validate the task files exist
            if (fileRepository.findAllByFileIdInAndStatusIsTrue(newReplacedPartDto.replacedPartFileIds).size != newReplacedPartDto.replacedPartFileIds.size) {
                throw EtnException(HttpStatus.BAD_REQUEST, "Error: At least one task file is invalid", "Al menos un archivo adjunto es inválido")
            }
        }
        // Create the replaced part
        val replacedPartEntity = ReplacedPart()
        replacedPartEntity.taskId = newReplacedPartDto.taskId
        replacedPartEntity.replacedPartDescription = newReplacedPartDto.replacedPartDescription
        replacedPartRepository.save(replacedPartEntity)
        logger.info("Replaced part created with id ${replacedPartEntity.replacedPartId}")
        // Create the replaced part files
        newReplacedPartDto.replacedPartFileIds.forEach { fileId ->
            val replacedPartFileEntity = ReplacedPartFile()
            replacedPartFileEntity.replacedPartId = replacedPartEntity.replacedPartId
            replacedPartFileEntity.fileId = fileId
            replacedPartFileRepository.save(replacedPartFileEntity)
        }
        logger.info("Replaced part files created for replaced part ${replacedPartEntity.replacedPartId}")
    }

    fun updateReplacedPart(
        replacedPartId: Long,
        newReplacedPartDto: NewReplacedPartDto
    ) {
        // Validate the replaced part is not empty
        if (newReplacedPartDto.replacedPartDescription.trim().isEmpty()) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Replaced part is blank", "La descripción del reemplazo está en blanco")
        }
        // Validate the replaced part exists
        val replacedPartEntity = replacedPartRepository.findByReplacedPartIdAndStatusIsTrue(replacedPartId)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: Replaced part not found", "Reemplazo no encontrado")
        // Validate that the task file ids are valid
        if (newReplacedPartDto.replacedPartFileIds.isNotEmpty()) {
            // Validate the task files exist
            if (fileRepository.findAllByFileIdInAndStatusIsTrue(newReplacedPartDto.replacedPartFileIds).size != newReplacedPartDto.replacedPartFileIds.size) {
                throw EtnException(HttpStatus.BAD_REQUEST, "Error: At least one task file is invalid", "Al menos un archivo adjunto es inválido")
            }
        }
        logger.info("Updating the replaced part with id $replacedPartId")
        // Update the replaced part
        replacedPartEntity.replacedPartDescription = newReplacedPartDto.replacedPartDescription
        replacedPartRepository.save(replacedPartEntity)
        logger.info("Replaced part updated with id $replacedPartId")
        // Delete previous replaced part files changing their status to false if they are different
        val replacedPartFileEntities = replacedPartFileRepository.findAllByReplacedPartIdAndStatusIsTrue(replacedPartId)
        // If they are different, update the replaced part files
        if (replacedPartFileEntities.map { it.fileId }.toSet() != newReplacedPartDto.replacedPartFileIds.map { it }.toSet()) {
            replacedPartFileEntities.forEach {
                it.status = false
                replacedPartFileRepository.save(it)
            }
            // Create the new replaced part files
            newReplacedPartDto.replacedPartFileIds.forEach { fileId ->
                val replacedPartFileEntity = ReplacedPartFile()
                replacedPartFileEntity.replacedPartId = replacedPartEntity.replacedPartId
                replacedPartFileEntity.fileId = fileId
                replacedPartFileRepository.save(replacedPartFileEntity)
            }
        }
        logger.info("Replaced part files updated for replaced part $replacedPartId")
    }

    fun deleteReplacedPart(replacedPartId: Long) {
        // Validate the replaced part exists
        val replacedPartEntity = replacedPartRepository.findByReplacedPartIdAndStatusIsTrue(replacedPartId)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: Replaced part not found", "Reemplazo no encontrado")
        // Delete the replaced part
        replacedPartEntity.status = false
        replacedPartRepository.save(replacedPartEntity)
    }
}
