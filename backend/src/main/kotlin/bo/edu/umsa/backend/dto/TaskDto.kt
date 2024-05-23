package bo.edu.umsa.backend.dto

import java.sql.Timestamp

data class TaskDto(
    val taskId: Int,
    val project: ProjectPartialDto,
    val taskStatus: TaskStatusDto,
    val taskPriority: TaskPriorityDto,
    val taskName: String,
    val taskDescription: String,
    val taskCreationDate: Timestamp,
    val taskDueDate: Timestamp,
    val createdBy: String,
    val rating: Int,
    val feedback: String,
    val taskAssignees: List<UserPartialDto>,
    val taskFiles: List<FilePartialDto>,
    val taskComments: List<TaskCommentDto>,
)
