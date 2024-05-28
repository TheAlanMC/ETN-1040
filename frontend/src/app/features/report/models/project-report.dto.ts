import {TaskReportDto} from "./task-report.dto";

export interface ProjectReportDto {
    projectId: number;
    projectName: string;
    projectStatusName: string;
    projectFinishedTasks: number;
    projectTotalTasks: number;
    projectDateFrom: Date;
    projectDateTo: Date;
    projectEndDate: Date;
    projectOwners: string[];
    projectModerators: string[];
    projectMembers: string[];
    taskReports: TaskReportDto[];
}
