package bo.edu.umsa.backend.dto

import java.sql.Timestamp

data class TaskPartialDto(
    val taskId: Int,
    val taskStatus: TaskStatusDto,
    val taskPriority: TaskPriorityDto,
    val taskName: String,
    val taskDescription: String,
    val taskDueDate: Timestamp,
    val taskEndDate: Timestamp?,
    val txDate: Timestamp,
    val taskAssignees: List<UserPartialDto>,
    val taskFiles: List<FilePartialDto>,
)