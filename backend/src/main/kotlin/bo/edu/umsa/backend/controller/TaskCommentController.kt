package bo.edu.umsa.backend.controller

import bo.edu.umsa.backend.dto.*
import bo.edu.umsa.backend.service.TaskCommentService
import bo.edu.umsa.backend.util.AuthUtil
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.*
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/task-comments")
class TaskCommentController @Autowired constructor(
    private val taskCommentService: TaskCommentService,
) {

    companion object {
        private val logger = LoggerFactory.getLogger(TaskCommentController::class.java.name)
    }

    @GetMapping("/{commentId}")
    fun getCommentById(
        @PathVariable commentId: Long
    ): ResponseEntity<ResponseDto<TaskCommentDto>> {
        logger.info("Starting the API call to get the comment by id")
        logger.info("GET /api/v1/task-comments/$commentId")
        AuthUtil.verifyAuthTokenHasRole("VER TAREAS")
        val comment: TaskCommentDto = taskCommentService.getCommentById(commentId)
        logger.info("Success: Comment retrieved")
        return ResponseEntity(ResponseDto(true, "Comentario recuperado", comment), HttpStatus.OK)
    }

    @PostMapping
    fun createComment(
        @RequestBody newTaskCommentDto: NewTaskCommentDto
    ): ResponseEntity<ResponseDto<Nothing>> {
        logger.info("Starting the API call to create the comment")
        logger.info("POST /api/v1/task-comments")
        AuthUtil.verifyAuthTokenHasRole("VER TAREAS")
        taskCommentService.createComment(newTaskCommentDto)
        logger.info("Success: Comment created")
        return ResponseEntity(ResponseDto(true, "El comentario se ha creado", null), HttpStatus.CREATED)
    }

    @PutMapping("/{commentId}")
    fun updateComment(
        @PathVariable commentId: Long,
        @RequestBody newTaskCommentDto: NewTaskCommentDto
    ): ResponseEntity<ResponseDto<Nothing>> {
        logger.info("Starting the API call to update the comment")
        logger.info("PUT /api/v1/task-comments/$commentId")
        AuthUtil.verifyAuthTokenHasRole("VER TAREAS")
        taskCommentService.updateComment(commentId, newTaskCommentDto)
        logger.info("Success: Comment updated")
        return ResponseEntity(ResponseDto(true, "El comentario se ha actualizado", null), HttpStatus.OK)
    }

    @DeleteMapping("/{commentId}")
    fun deleteComment(
        @PathVariable commentId: Long
    ): ResponseEntity<ResponseDto<Nothing>> {
        logger.info("Starting the API call to delete the comment")
        logger.info("DELETE /api/v1/task-comments/$commentId")
        AuthUtil.verifyAuthTokenHasRole("VER TAREAS")
        taskCommentService.deleteComment(commentId)
        logger.info("Success: Comment deleted")
        return ResponseEntity(ResponseDto(true, "El comentario se ha eliminado", null), HttpStatus.OK)
    }
}