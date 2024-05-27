package bo.edu.umsa.backend.dto

data class TaskReportFiltersDto (
    val projects: List<TaskReportProjectDto>,
    val statuses: List<TaskReportStatusDto>,
    val priorities: List<TaskReportPriorityDto>,
    val taskAssignees: List<TaskReportAssigneeDto>,
)

data class TaskReportProjectDto (
    val projectId: Int,
    val projectName: String,
)

data class TaskReportStatusDto (
    val statusId: Int,
    val statusName: String,
)

data class TaskReportPriorityDto (
    val priorityId: Int,
    val priorityName: String,
)

data class TaskReportAssigneeDto (
    val userId: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
)