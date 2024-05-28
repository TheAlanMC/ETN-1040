export interface ProjectDashboardDto {
    totalProjects: number;
    totalCompletedProjects: number;
    totalCompletedWithDelayProjects: number;
    totalInProgressProjects: number;
    totalDelayedProjects: number;
    projectByDate: ProjectByDateDto[];
}

export interface ProjectByDateDto {
    year: number;
    month: number;
    completedProjects: number;
    completedWithDelayProjects: number;
    inProgressProjects: number;
    delayedProjects: number;
}
