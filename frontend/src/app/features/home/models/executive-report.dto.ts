import {ProjectReportDto} from "./project-report.dto";

export interface ExecutiveReportDto {
    completedProjects: number;
    completedTasks: number;
    completedWithDelayProjects: number;
    completedWithDelayTasks: number;
    delayedProjects: number;
    delayedTasks: number;
    highPriorityTasks: number;
    inProgressProjects: number;
    inProgressTasks: number;
    lowPriorityTasks: number;
    mediumPriorityTasks: number;
    pendingTasks: number;
    projectReports: ProjectReportDto[];
    totalProjects: number;
    totalTasks: number;

}
