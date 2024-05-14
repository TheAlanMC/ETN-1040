package bo.edu.umsa.backend.dto

import java.sql.Timestamp

data class TaskCommentDto(
    val taskCommentId: Int,
    val user: UserPartialDto,
    val commentNumber: Int,
    val comment: String,
    val commentDate: Timestamp,
    val taskCommentFileIds: List<Int>
)
