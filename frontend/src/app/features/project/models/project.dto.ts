export interface ProjectDto {
  projectId: number;
  projectName: string;
  projectDescription: string;
  dateFrom: Date;
  dateTo: Date;
  projectOwnerIds: number[];
  projectModeratorIds: number[];
  projectMemberIds: number[];
}
