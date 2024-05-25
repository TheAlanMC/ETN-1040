import {UserDto} from "../../user/models/user.dto";
import {FileDto} from "../../../core/models/file.dto";

export interface TaskCommentDto {
    taskCommentId: number;
    user: UserDto;
    taskCommentNumber: number;
    taskComment: string;
    txDate: Date;
    taskCommentFiles: FileDto[];
}
