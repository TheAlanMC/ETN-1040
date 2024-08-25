package bo.edu.umsa.backend.dto

data class CurrentAssistantScheduleDto(
    val assistantId: Int,
    val assistant: UserPartialDto,
    val schedule: ScheduleDto,
)
