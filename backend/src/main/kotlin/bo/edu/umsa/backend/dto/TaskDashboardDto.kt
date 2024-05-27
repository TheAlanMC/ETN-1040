package bo.edu.umsa.backend.dto

data class TaskDashboardDto(
    val totalTasks: Int,
    val totalCompletedTasks: Int,
    val totalCompletedWithDelayTasks: Int,
    val totalInProgressTasks: Int,
    val totalPendingTasks: Int,
    val totalDelayedTasks: Int,
    val totalHighPriorityTasks: Int,
    val totalMediumPriorityTasks: Int,
    val totalLowPriorityTasks: Int,
    val taskByDate: List<TaskByDateDto>,
)

data class TaskByDateDto(
    val year: Int,
    val month: Int,
    val completedTasks: Int,
    val completedWithDelayTasks: Int,
    val inProgressTasks: Int,
    val pendingTasks: Int,
    val delayedTasks: Int,
)
