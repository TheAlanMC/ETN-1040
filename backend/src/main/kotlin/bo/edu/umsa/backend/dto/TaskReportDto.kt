package bo.edu.umsa.backend.dto

import java.sql.Timestamp

data class TaskReportDto(
    val taskId: Int,
    val taskName: String,
    val projectName: String,
    val taskStatusName: String,
    val taskPriorityName: String,
    val taskCreationDate: Timestamp,
    val taskDueDate: Timestamp,
    val taskEndDate: Timestamp?,
    val taskRating: Int,
    val replacedPartDescription: String,
    val taskAssignees: List<String>,
)

