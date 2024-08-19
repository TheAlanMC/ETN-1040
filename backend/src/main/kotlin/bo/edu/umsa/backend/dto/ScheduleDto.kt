package bo.edu.umsa.backend.dto

import java.time.LocalTime

data class ScheduleDto(
    val scheduleId: Int,
    val dayOfWeek: String,
    val dayNumber: Number,
    val hourFrom: LocalTime,
    val hourTo: LocalTime,
)