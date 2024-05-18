import {UserDto} from "../../user/models/user.dto";

export interface ProjectDto {
    projectId: number;
    projectName: string;
    projectDescription: string;
    dateFrom: Date;
    dateTo: Date;
    projectOwners: UserDto[];
    projectModerators: UserDto[];
    projectMembers: UserDto[];
}
