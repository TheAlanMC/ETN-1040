import {UserDto} from "../../user/models/user.dto";

export interface TaskHistoryDto {
    user: UserDto;
    fieldName: string;
    previousValue: string;
    newValue: string;
    txDate: Date;
}
