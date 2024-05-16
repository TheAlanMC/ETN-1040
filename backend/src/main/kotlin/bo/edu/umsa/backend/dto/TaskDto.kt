package bo.edu.umsa.backend.dto

import java.sql.Timestamp

data class TaskDto(
    val taskId: Int,
    val taskStatus: TaskStatusDto,
    val project: ProjectPartialDto,
    val taskName: String,
    val taskDescription: String,
    val taskCreationDate: Timestamp,
    val taskDeadline: Timestamp,
    val createdBy: String,
    val taskPriority: Int,
    val taskAssigneeIds: List<Int>,
    val taskFileIds: List<Int>,
)
