package bo.edu.umsa.backend.dto

data class NewTaskDto(
    val projectId: Int,
    val taskName: String,
    val taskDescription: String,
    val taskDeadline: String,
    val taskPriority: Int,
    val taskAssigneeIds: List<Int>,
    val taskFileIds: List<Int>
)