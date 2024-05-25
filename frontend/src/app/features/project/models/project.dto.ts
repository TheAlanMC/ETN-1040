import {UserDto} from "../../user/models/user.dto";

export interface ProjectDto {
    projectId: number;
    projectName: string;
    projectDescription: string;
    projectDateFrom: Date;
    projectDateTo: Date;
    projectEndDate: Date | null;
    projectOwners: UserDto[];
    projectModerators: UserDto[];
    projectMembers: UserDto[];
}
