export interface TaskReportDto {
    taskId: number;
    taskName: string;
    projectName: string;
    taskStatusName: string;
    taskPriorityName: string;
    taskCreationDate: Date;
    taskDueDate: Date;
    taskEndDate: Date;
    taskRating: number;
    replacedPartDescription: string;
    taskAssignees: string[];
}
