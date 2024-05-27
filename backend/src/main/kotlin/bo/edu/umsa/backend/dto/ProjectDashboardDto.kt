package bo.edu.umsa.backend.dto

data class ProjectDashboardDto(
    val totalProjects: Int,
    val totalCompletedProjects: Int,
    val totalCompletedWithDelayProjects: Int,
    val totalInProgressProjects: Int,
    val totalDelayedProjects: Int,
    val projectByDate: List<ProjectByDateDto>,
)

data class ProjectByDateDto(
    val year: Int,
    val month: Int,
    val completedProjects: Int,
    val completedWithDelayProjects: Int,
    val inProgressProjects: Int,
    val delayedProjects: Int,
)
