package bo.edu.umsa.backend.dto

import java.sql.Timestamp

data class TaskCommentDto(
    val taskCommentId: Int,
    val user: UserPartialDto,
    val taskCommentNumber: Int,
    val taskComment: String,
    val txDate: Timestamp,
    val taskCommentFiles: List<FilePartialDto>,
)
