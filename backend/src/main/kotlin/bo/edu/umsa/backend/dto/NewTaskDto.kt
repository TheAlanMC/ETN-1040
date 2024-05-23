package bo.edu.umsa.backend.dto

data class NewTaskDto(
    val projectId: Int,
    val taskPriorityId: Int,
    val taskName: String,
    val taskDescription: String,
    val taskDueDate: String,
    val taskAssigneeIds: List<Int>,
    val taskFileIds: List<Int>,
)