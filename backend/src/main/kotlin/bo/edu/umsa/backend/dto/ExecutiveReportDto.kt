package bo.edu.umsa.backend.dto

data class ExecutiveReportDto (
    val totalProjects: Int,
    val completedProjects: Int,
    val completedWithDelayProjects: Int,
    val inProgressProjects: Int,
    val delayedProjects: Int,
    val totalTasks: Int,
    val completedTasks: Int,
    val completedWithDelayTasks: Int,
    val inProgressTasks: Int,
    val pendingTasks: Int,
    val delayedTasks: Int,
    val highPriorityTasks: Int,
    val mediumPriorityTasks: Int,
    val lowPriorityTasks: Int,
    val projectReports: List<ProjectReportDto>,
)