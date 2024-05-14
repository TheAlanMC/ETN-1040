package bo.edu.umsa.backend.dto

import java.sql.Timestamp

data class TaskDto(
    val taskId: Int,
    val taskStatusId: Int,
    val taskStatus: String,
    val taskName: String,
    val taskDescription: String,
    val taskDeadline: Timestamp,
    val taskPriority: Int,
    val taskAssigneeIds: List<Int>,
)
