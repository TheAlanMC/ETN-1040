import {TaskDto} from "./task.dto";

export interface TaskListDto {
  listId: string;
  title?: string;
  tasks: TaskDto[];
}
