package bo.edu.umsa.backend.mapper

import bo.edu.umsa.backend.dto.ScheduleDto
import bo.edu.umsa.backend.entity.Schedule

class ScheduleMapper {
    companion object {
        fun entityToDto(schedule: Schedule): ScheduleDto {
            return ScheduleDto(
                scheduleId = schedule.scheduleId,
                dayOfWeek = schedule.dayOfWeek,
                dayNumber = schedule.dayNumber,
                hourFrom = schedule.hourFrom,
                hourTo = schedule.hourTo,
            )
        }
    }
}
