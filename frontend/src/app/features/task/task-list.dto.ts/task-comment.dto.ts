import {UserDto} from "../../user/models/user.dto";

export interface TaskCommentDto {
  taskCommentId: number;
  user: UserDto;
  commentNumber: number;
  comment: string;
  commentDate: Date;
  taskCommentFileIds: number[];
}
