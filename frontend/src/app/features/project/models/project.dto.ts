import {UserDto} from "../../user/models/user.dto";

export interface ProjectDto {
    projectId: number;
    projectName: string;
    projectDescription: string;
    projectDateFrom: Date;
    projectDateTo: Date;
    projectObjective: string;
    projectCloseMessage: string;
    projectEndDate: Date | null;
    projectOwners: UserDto[];
    projectModerators: UserDto[];
    projectMembers: UserDto[];
    finishedTasks: number;
    totalTasks: number;
}
