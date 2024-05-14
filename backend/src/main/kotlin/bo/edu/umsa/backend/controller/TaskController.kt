package bo.edu.umsa.backend.controller

import bo.edu.umsa.backend.dto.*
import bo.edu.umsa.backend.service.TaskService
import bo.edu.umsa.backend.util.AuthUtil
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.*
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/tasks")
class TaskController @Autowired constructor(
    private val taskService: TaskService,
) {

    companion object {
        private val logger = LoggerFactory.getLogger(TaskController::class.java.name)
    }

    @PostMapping
    fun createTask(
        @RequestBody newTaskDto: NewTaskDto
    ): ResponseEntity<ResponseDto<Nothing>> {
        logger.info("Starting the API call to create the task")
        logger.info("POST /api/v1/tasks")
        AuthUtil.verifyAuthTokenHasRole("CREAR TAREAS")
        taskService.createTask(newTaskDto)
        logger.info("Success: Task created")
        return ResponseEntity(ResponseDto(true, "La tarea se ha creado", null), HttpStatus.CREATED)
    }



}