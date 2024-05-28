export interface TaskReportFilterDto {
    projects: TaskReportProjectDto[];
    statuses: TaskReportStatusDto[];
    priorities: TaskReportPriorityDto[];
    taskAssignees: TaskReportAssigneeDto[];
}

export interface TaskReportProjectDto {
    projectId: number;
    projectName: string;
}

export interface TaskReportStatusDto {
    statusId: number;
    statusName: string;
}

export interface TaskReportPriorityDto {
    priorityId: number;
    priorityName: string;
}

export interface TaskReportAssigneeDto {
    userId: number;
    firstName: string;
    lastName: string;
    email: string;
}
