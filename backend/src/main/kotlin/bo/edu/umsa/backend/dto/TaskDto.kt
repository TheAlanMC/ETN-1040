package bo.edu.umsa.backend.dto

import java.sql.Timestamp

data class TaskDto(
    val taskId: Int,
    val project: ProjectPartialDto,
    val taskStatus: TaskStatusDto,
    val taskPriority: TaskPriorityDto,
    val taskName: String,
    val taskDescription: String,
    val taskDueDate: Timestamp,
    val taskEndDate: Timestamp?,
    val taskRating: Int,
    val taskRatingComment: String,
    val txDate: Timestamp,
    val txHost: String,
    val taskAssignees: List<UserPartialDto>,
    val taskFiles: List<FilePartialDto>,
    val taskComments: List<TaskCommentDto>,
    val replacedParts: List<ReplacedPartDto>,
)

