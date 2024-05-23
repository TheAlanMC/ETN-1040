package bo.edu.umsa.backend.dto

data class NewTaskCommentDto(
    val taskId: Int,
    val taskComment: String,
    val taskCommentFileIds: List<Int>,
)