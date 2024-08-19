package bo.edu.umsa.backend.dto

data class NewAssistantScheduleDto(
    val assistantId: Int,
    val scheduleIds: List<Int>,
)
