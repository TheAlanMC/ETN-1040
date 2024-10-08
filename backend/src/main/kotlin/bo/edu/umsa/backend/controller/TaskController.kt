package bo.edu.umsa.backend.controller

import bo.edu.umsa.backend.dto.*
import bo.edu.umsa.backend.service.TaskService
import bo.edu.umsa.backend.util.AuthUtil
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/tasks")
class TaskController @Autowired constructor(
    private val taskService: TaskService,
) {

    companion object {
        private val logger = LoggerFactory.getLogger(TaskController::class.java.name)
    }

    @GetMapping("/statuses")
    fun getAllStatuses(): ResponseEntity<ResponseDto<List<TaskStatusDto>>> {
        logger.info("Starting the API call to get all task statuses")
        logger.info("GET /api/v1/tasks/statuses")
        AuthUtil.verifyAuthTokenHasPermissions(listOf("VER TAREAS", "CREAR TAREAS", "EDITAR TAREAS").toTypedArray())
        val statuses: List<TaskStatusDto> = taskService.getAllStatuses()
        logger.info("Success: Task statuses retrieved")
        return ResponseEntity(ResponseDto(true, "Estados de tareas recuperados", statuses), HttpStatus.OK)
    }

    @GetMapping("/priorities")
    fun getAllPriorities(): ResponseEntity<ResponseDto<List<TaskPriorityDto>>> {
        logger.info("Starting the API call to get all task priorities")
        logger.info("GET /api/v1/tasks/priorities")
        AuthUtil.verifyAuthTokenHasPermissions(listOf("VER TAREAS", "CREAR TAREAS", "EDITAR TAREAS").toTypedArray())
        val priorities: List<TaskPriorityDto> = taskService.getAllPriorities()
        logger.info("Success: Task priorities retrieved")
        return ResponseEntity(ResponseDto(true, "Prioridades de tareas recuperadas", priorities), HttpStatus.OK)
    }

    @GetMapping
    fun getTasks(
        @RequestParam(defaultValue = "taskId") sortBy: String,
        @RequestParam(defaultValue = "asc") sortType: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(required = false) keyword: String?,
        @RequestParam(required = false) statuses: List<String>?,
        @RequestParam(required = false) priorities: List<String>?,
        @RequestParam(required = false) dateFrom: String?,
        @RequestParam(required = false) dateTo: String?,
    ): ResponseEntity<ResponseDto<Page<TaskPartialDto>>> {
        logger.info("Starting the API call to get the tasks")
        logger.info("GET /api/v1/tasks")
        AuthUtil.verifyAuthTokenHasPermission("VER TAREAS")
        val tasks: Page<TaskPartialDto> = taskService.getTasks(sortBy, sortType, page, size, keyword, statuses, priorities, dateFrom, dateTo)
        logger.info("Success: Tasks retrieved")
        return ResponseEntity(ResponseDto(true, "Tareas recuperadas", tasks), HttpStatus.OK)
    }

    @GetMapping("/{taskId}")
    fun getTaskById(@PathVariable taskId: Long): ResponseEntity<ResponseDto<TaskDto>> {
        logger.info("Starting the API call to get the task by id")
        logger.info("GET /api/v1/tasks/$taskId")
        AuthUtil.verifyAuthTokenHasPermission("VER TAREAS")
        val task: TaskDto = taskService.getTaskById(taskId)
        logger.info("Success: Task retrieved")
        return ResponseEntity(ResponseDto(true, "Tarea recuperada", task), HttpStatus.OK)
    }

    @PostMapping
    fun createTask(@RequestBody newTaskDto: NewTaskDto): ResponseEntity<ResponseDto<Nothing>> {
        logger.info("Starting the API call to create the task")
        logger.info("POST /api/v1/tasks")
        AuthUtil.verifyAuthTokenHasPermission("CREAR TAREAS")
        taskService.createTask(newTaskDto)
        logger.info("Success: Task created")
        return ResponseEntity(ResponseDto(true, "La tarea se ha creado", null), HttpStatus.CREATED)
    }

    @PutMapping("/{taskId}")
    fun updateTask(
        @PathVariable taskId: Long,
        @RequestBody newTaskDto: NewTaskDto
    ): ResponseEntity<ResponseDto<Nothing>> {
        logger.info("Starting the API call to update the task")
        logger.info("PUT /api/v1/tasks/$taskId")
        AuthUtil.verifyAuthTokenHasPermission("EDITAR TAREAS")
        taskService.updateTask(taskId, newTaskDto)
        logger.info("Success: Task updated")
        return ResponseEntity(ResponseDto(true, "La tarea se ha actualizado", null), HttpStatus.OK)
    }

    @PutMapping("/{taskId}/status")
    fun updateTaskStatus(
        @PathVariable taskId: Long,
        @RequestBody taskStatusDto: TaskStatusDto
    ): ResponseEntity<ResponseDto<Nothing>> {
        logger.info("Starting the API call to update the task status")
        logger.info("PUT /api/v1/tasks/$taskId/status")
        AuthUtil.verifyAuthTokenHasPermissions(listOf("VER TAREAS", "EDITAR TAREAS").toTypedArray())
        taskService.updateTaskStatus(taskId, taskStatusDto.taskStatusId.toLong())
        logger.info("Success: Task status updated")
        return ResponseEntity(ResponseDto(true, "El estado de la tarea se ha actualizado", null), HttpStatus.OK)
    }

    @DeleteMapping("/{taskId}")
    fun deleteTask(@PathVariable taskId: Long): ResponseEntity<ResponseDto<Nothing>> {
        logger.info("Starting the API call to delete the task")
        logger.info("DELETE /api/v1/tasks/$taskId")
        AuthUtil.verifyAuthTokenHasPermission("EDITAR TAREAS")
        taskService.deleteTask(taskId)
        logger.info("Success: Task deleted")
        return ResponseEntity(ResponseDto(true, "La tarea se ha eliminado", null), HttpStatus.OK)
    }

    @GetMapping("/{taskId}/history/all")
    fun getTaskHistory(@PathVariable taskId: Long): ResponseEntity<ResponseDto<List<TaskHistoryDto>>> {
        logger.info("Starting the API call to get the task history")
        logger.info("GET /api/v1/tasks/$taskId/history/all")
        AuthUtil.verifyAuthTokenHasPermission("VER TAREAS")
        val taskHistory: List<TaskHistoryDto> = taskService.getTaskHistory(taskId)
        logger.info("Success: Task history retrieved")
        return ResponseEntity(ResponseDto(true, "Historial de la tarea recuperado", taskHistory), HttpStatus.OK)
    }

    @PostMapping("/{taskId}/feedback")
    fun createTaskFeedback(
        @PathVariable taskId: Long,
        @RequestBody newTaskFeedbackDto: NewTaskFeedbackDto
    ): ResponseEntity<ResponseDto<Nothing>> {
        logger.info("Starting the API call to create the task feedback")
        logger.info("POST /api/v1/tasks/$taskId/feedback")
        AuthUtil.verifyAuthTokenHasPermission("VER TAREAS")
        taskService.createTaskFeedback(taskId, newTaskFeedbackDto)
        logger.info("Success: Task feedback created")
        return ResponseEntity(ResponseDto(true, "El feedback de la tarea se ha creado", null), HttpStatus.CREATED)
    }
}