package bo.edu.umsa.backend.dto

data class AssistantScheduleDto(
    val assistantId: Int,
    val assistant: UserPartialDto,
    val schedules: List<ScheduleDto>,
)
