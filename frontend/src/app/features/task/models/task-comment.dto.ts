import {UserDto} from "../../user/models/user.dto";
import {FileDto} from "../../../core/models/file.dto";

export interface TaskCommentDto {
    taskCommentId: number;
    user: UserDto;
    commentNumber: number;
    comment: string;
    commentDate: Date;
    taskCommentFiles: FileDto[];
}
