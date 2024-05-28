export interface TaskDashboardDto {
    totalTasks: number;
    totalCompletedTasks: number;
    totalCompletedWithDelayTasks: number;
    totalInProgressTasks: number;
    totalPendingTasks: number;
    totalDelayedTasks: number;
    totalHighPriorityTasks: number;
    totalMediumPriorityTasks: number;
    totalLowPriorityTasks: number;
    taskByDate: TaskByDateDto[];
}

export interface TaskByDateDto {
    year: number;
    month: number;
    completedTasks: number;
    completedWithDelayTasks: number;
    inProgressTasks: number;
    pendingTasks: number;
    delayedTasks: number;
}
