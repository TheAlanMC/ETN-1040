import {TaskStatusDto} from "./task-status.dto";
import {ProjectDto} from "../../project/models/project.dto";
import {UserDto} from "../../user/models/user.dto";
import {FileDto} from "../../../core/models/file.dto";
import {TaskCommentDto} from "./task-comment.dto";

export interface TaskDto {
    taskId: number;
    taskStatus: TaskStatusDto;
    project: ProjectDto;
    taskName: string;
    taskDescription: string;
    taskCreationDate: Date;
    taskDeadline: Date;
    createdBy: string;
    taskPriority: number;
    taskAssignees: UserDto[];
    taskFiles: FileDto[];
    taskComments: TaskCommentDto[];
}
