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
    val rating: Int,
    val feedback: String,
    val taskAssignees: List<UserPartialDto>,
    val taskFiles: List<FilePartialDto>,
    val taskComments: List<TaskCommentDto>,
)
