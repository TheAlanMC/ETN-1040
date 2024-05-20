import {UserDto} from "../../user/models/user.dto";

export interface TaskHistoryDto {
    createdDate: Date;
    createdBy: UserDto;
    previousValue: string;
    newValue: string;
}
