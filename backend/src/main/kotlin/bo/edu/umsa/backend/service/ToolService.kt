package bo.edu.umsa.backend.service

import bo.edu.umsa.backend.dto.NewToolDto
import bo.edu.umsa.backend.dto.ToolDto
import bo.edu.umsa.backend.entity.LoanedTool
import bo.edu.umsa.backend.entity.Tool
import bo.edu.umsa.backend.exception.EtnException
import bo.edu.umsa.backend.mapper.ToolMapper
import bo.edu.umsa.backend.repository.*
import bo.edu.umsa.backend.util.AuthUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class ToolService @Autowired constructor(private val fileRepository: FileRepository, private val toolRepository: ToolRepository, private val loanedToolRepository: LoanedToolRepository, private val taskRepository: TaskRepository, private val taskAssigneeRepository: TaskAssigneeRepository) {
    companion object {
        private val logger = org.slf4j.LoggerFactory.getLogger(ToolService::class.java)
    }

    fun getAllTools(): List<ToolDto> {
        logger.info("Getting all tools")
        val tools = toolRepository.findAllByStatusIsTrue()
        return tools.map { ToolMapper.entityToDto(it) }
    }

    fun getToolById(toolId: Long): ToolDto {
        logger.info("Getting the tool with id $toolId")
        val tool = toolRepository.findByToolIdAndStatusIsTrue(toolId)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: Tool not found", "Herramienta no encontrada")
        return ToolMapper.entityToDto(tool)
    }

    fun createTool(newToolDto: NewToolDto) {
        // Validate the tool code and tool name are not empty
        if (newToolDto.toolCode.isEmpty() || newToolDto.toolName.isEmpty()) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: At least one required field is blank", "Al menos un campo requerido está en blanco")
        }
        // Validate the file photo id is valid
        val fileEntity = fileRepository.findByFileIdAndStatusIsTrue(newToolDto.filePhotoId.toLong())
            ?: throw EtnException(HttpStatus.BAD_REQUEST, "Error: File photo is invalid", "La foto del archivo es inválida")
        // Validate the file is an image
        if (!fileEntity.isPicture) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: File photo is not an image", "La foto del archivo no es una imagen")
        }
        logger.info("Creating a new tool")
        // Create the tool
        val toolEntity = Tool()
        toolEntity.filePhotoId = newToolDto.filePhotoId
        toolEntity.toolCode = newToolDto.toolCode
        toolEntity.toolName = newToolDto.toolName
        toolEntity.toolDescription = newToolDto.toolDescription
        toolRepository.save(toolEntity)
        logger.info("Tool created with id ${toolEntity.toolId}")
    }

    fun updateTool(toolId: Long, newToolDto: NewToolDto) {
        // Validate the tool code and tool name are not empty
        if (newToolDto.toolCode.isEmpty() || newToolDto.toolName.isEmpty()) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: At least one required field is blank", "Al menos un campo requerido está en blanco")
        }
        // Validate the file photo id is valid
        val fileEntity = fileRepository.findByFileIdAndStatusIsTrue(newToolDto.filePhotoId.toLong())
            ?: throw EtnException(HttpStatus.BAD_REQUEST, "Error: File photo is invalid", "La foto del archivo es inválida")
        // Validate the file is an image
        if (!fileEntity.isPicture) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: File photo is not an image", "La foto del archivo no es una imagen")
        }
        // Validate the tool exists
        val toolEntity = toolRepository.findByToolIdAndStatusIsTrue(toolId)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: Tool not found", "Herramienta no encontrada")
        logger.info("Updating the tool with id $toolId")
        // Update the tool
        toolEntity.filePhotoId = newToolDto.filePhotoId
        toolEntity.toolCode = newToolDto.toolCode
        toolEntity.toolName = newToolDto.toolName
        toolEntity.toolDescription = newToolDto.toolDescription
        toolRepository.save(toolEntity)
        logger.info("Tool updated with id $toolId")
    }

    fun deleteTool(toolId: Long) {
        // Validate the tool exists
        val toolEntity = toolRepository.findByToolIdAndStatusIsTrue(toolId)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: Tool not found", "Herramienta no encontrada")
        // Validate the tool is not loaned
        if (loanedToolRepository.findByToolIdAndStatusIsTrue(toolId) != null) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Tool is loaned", "La herramienta está prestada")
        }
        logger.info("Deleting the tool with id $toolId")
        // Delete the tool
        toolEntity.status = false
        toolRepository.save(toolEntity)
        logger.info("Tool deleted with id $toolId")
    }

    fun assignToolToTask(toolId: Long, taskId: Long) {
        // Get the user id from the token
        val userId = AuthUtil.getUserIdFromAuthToken() ?: throw EtnException(HttpStatus.UNAUTHORIZED, "Error: Unauthorized", "No autorizado")
        // Validate the task exists
        taskRepository.findByTaskIdAndStatusIsTrue(taskId) ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: Task not found", "Tarea no encontrada")
        // Validate the tool exists
        toolRepository.findByToolIdAndStatusIsTrue(toolId)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: Tool not found", "Herramienta no encontrada")
        // Validate the user is assigned to the task
        taskAssigneeRepository.findByTaskIdAndUserIdAndStatusIsTrue(taskId, userId)
            ?: throw EtnException(HttpStatus.FORBIDDEN, "Error: User is not assigned to the task", "El usuario no está asignado a la tarea")
        logger.info("Assigning the tool with id $toolId to the task with id $taskId")
        // Assign the tool to the task
        val loanedToolEntity = LoanedTool()
        loanedToolEntity.toolId = toolId.toInt()
        loanedToolEntity.taskId = taskId.toInt()
        loanedToolEntity.userId = userId.toInt()
        loanedToolRepository.save(loanedToolEntity)
        logger.info("Tool assigned with id $toolId to the task with id $taskId")
    }
}
