export interface ProjectReportFilterDto {
    statuses: ProjectReportStatusDto[];
    projectOwners: ProjectReportOwnerDto[];
    projectModerators: ProjectReportModeratorDto[];
    projectMembers: ProjectReportMemberDto[];
}

export interface ProjectReportStatusDto {
    statusId: number;
    statusName: string;
}

export interface ProjectReportOwnerDto {
    userId: number;
    firstName: string;
    lastName: string;
    email: string;
}

export interface ProjectReportModeratorDto {
    userId: number;
    firstName: string;
    lastName: string;
    email: string;
}

export interface ProjectReportMemberDto {
    userId: number;
    firstName: string;
    lastName: string;
    email: string;
}
