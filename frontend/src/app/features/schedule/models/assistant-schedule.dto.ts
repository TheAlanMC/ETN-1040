import {UserDto} from "../../user/models/user.dto";
import {ScheduleDto} from "./schedule.dto";

export interface AssistantScheduleDto {
    assistantId: number;
    assistant: UserDto;
    schedules: ScheduleDto;
}
