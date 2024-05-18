package bo.edu.umsa.backend.dto

import java.sql.Timestamp

data class TaskPartialDto(
    val taskId: Int,
    val taskStatus: TaskStatusDto,
    val taskName: String,
    val taskDescription: String,
    val taskCreationDate: Timestamp,
    val taskDeadline: Timestamp,
    val taskPriority: Int,
    val taskAssignees: List<UserPartialDto>,
    val taskFiles: List<FilePartialDto>,
)
