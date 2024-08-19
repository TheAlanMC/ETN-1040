import {UserDto} from "../../user/models/user.dto";
import {ScheduleDto} from "./schedule.dto";

export interface AssistantDto {
    assistantId: number;
    assistant: UserDto;
    scheduleIds: number[];
    schedules: ScheduleDto[];
}
