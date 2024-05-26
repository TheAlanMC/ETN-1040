import {TaskStatusDto} from "./task-status.dto";
import {ProjectDto} from "../../project/models/project.dto";
import {UserDto} from "../../user/models/user.dto";
import {FileDto} from "../../../core/models/file.dto";
import {TaskCommentDto} from "./task-comment.dto";
import {TaskPriorityDto} from "./task-priority.dto";
import {ReplacedPartDto} from "./replaced-part.dto";

export interface TaskDto {
    taskId: number;
    projectId: number;
    project: ProjectDto;
    taskStatus: TaskStatusDto;
    taskPriority: TaskPriorityDto;
    taskName: string;
    taskDescription: string;
    taskDueDate: Date;
    taskEndDate: Date | null;
    taskRating: number;
    taskRatingComment: string;
    txDate: Date;
    txHost: string;
    taskAssignees: UserDto[];
    taskFiles: FileDto[];
    taskComments: TaskCommentDto[];
    replacedParts: ReplacedPartDto[];
}
