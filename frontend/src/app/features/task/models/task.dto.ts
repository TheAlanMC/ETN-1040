import {TaskStatusDto} from "./task-status.dto";
import {ProjectDto} from "../../project/models/project.dto";

export interface TaskDto {
  taskId: number;
  taskStatus: TaskStatusDto;
  project: ProjectDto;
  taskName: string;
  taskDescription: string;
  taskCreationDate: Date;
  taskDeadline: Date;
  taskPriority: number;
  taskAssigneeIds: number[];
  taskFileIds: number[];
}
