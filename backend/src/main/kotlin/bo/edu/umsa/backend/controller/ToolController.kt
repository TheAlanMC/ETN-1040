package bo.edu.umsa.backend.controller

import bo.edu.umsa.backend.dto.*
import bo.edu.umsa.backend.service.ToolService
import bo.edu.umsa.backend.util.AuthUtil
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.*
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/tools")
class ToolController @Autowired constructor(
    private val toolService: ToolService
) {

    companion object {
        private val logger = LoggerFactory.getLogger(ToolController::class.java.name)
    }

    @GetMapping
    fun getAllTools(): ResponseEntity<ResponseDto<List<ToolDto>>> {
        logger.info("Starting the API call to get all tools")
        logger.info("GET /api/v1/tools")
//        AuthUtil.verifyAuthTokenHasRole("VER HERRAMIENTAS")
        val tools: List<ToolDto> = toolService.getAllTools()
        logger.info("Success: Tools retrieved")
        return ResponseEntity(ResponseDto(true, "Herramientas recuperadas", tools), HttpStatus.OK)
    }

    @GetMapping("/{toolId}")
    fun getToolById(
        @PathVariable toolId: Long
    ): ResponseEntity<ResponseDto<ToolDto>> {
        logger.info("Starting the API call to get the tool by id")
        logger.info("GET /api/v1/tools/$toolId")
//        AuthUtil.verifyAuthTokenHasRole("VER HERRAMIENTAS")
        val tool: ToolDto = toolService.getToolById(toolId)
        logger.info("Success: Tool retrieved")
        return ResponseEntity(ResponseDto(true, "Herramienta recuperada", tool), HttpStatus.OK)
    }

    @PostMapping
    fun createTool(
        @RequestBody newToolDto: NewToolDto
    ): ResponseEntity<ResponseDto<Nothing>> {
        logger.info("Starting the API call to create the tool")
        logger.info("POST /api/v1/tools")
//        AuthUtil.verifyAuthTokenHasRole("CREAR HERRAMIENTAS")
        toolService.createTool(newToolDto)
        logger.info("Success: Tool created")
        return ResponseEntity(ResponseDto(true, "La herramienta se ha creado", null), HttpStatus.CREATED)
    }

    @PutMapping("/{toolId}")
    fun updateTool(
        @PathVariable toolId: Long,
        @RequestBody newToolDto: NewToolDto
    ): ResponseEntity<ResponseDto<Nothing>> {
        logger.info("Starting the API call to update the tool")
        logger.info("PUT /api/v1/tools/$toolId")
//        AuthUtil.verifyAuthTokenHasRole("EDITAR HERRAMIENTAS")
        toolService.updateTool(toolId, newToolDto)
        logger.info("Success: Tool updated")
        return ResponseEntity(ResponseDto(true, "La herramienta se ha actualizado", null), HttpStatus.OK)
    }

    @DeleteMapping("/{toolId}")
    fun deleteTool(
        @PathVariable toolId: Long
    ): ResponseEntity<ResponseDto<Nothing>> {
        logger.info("Starting the API call to delete the tool")
        logger.info("DELETE /api/v1/tools/$toolId")
//        AuthUtil.verifyAuthTokenHasRole("EDITAR HERRAMIENTAS")
        toolService.deleteTool(toolId)
        logger.info("Success: Tool deleted")
        return ResponseEntity(ResponseDto(true, "La herramienta se ha eliminado", null), HttpStatus.OK)
    }

    @PostMapping("/{toolId}/tasks/{taskId}")
    fun assignToolToTask(
        @PathVariable toolId: Long,
        @PathVariable taskId: Long
    ): ResponseEntity<ResponseDto<Nothing>> {
        logger.info("Starting the API call to assign the tool to the task")
        logger.info("POST /api/v1/tools/$toolId/tasks/$taskId")
//        AuthUtil.verifyAuthTokenHasRole("EDITAR HERRAMIENTAS")
        toolService.assignToolToTask(toolId, taskId)
        logger.info("Success: Tool assigned to task")
        return ResponseEntity(ResponseDto(true, "La herramienta se ha asignado a la tarea", null), HttpStatus.OK)
    }
}