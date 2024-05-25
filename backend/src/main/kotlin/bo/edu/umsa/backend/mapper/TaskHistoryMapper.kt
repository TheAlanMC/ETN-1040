package bo.edu.umsa.backend.mapper

import bo.edu.umsa.backend.dto.TaskHistoryDto
import bo.edu.umsa.backend.entity.TaskHistory

class TaskHistoryMapper {
    companion object {
        fun entityToDto(taskHistory: TaskHistory): TaskHistoryDto {
            return TaskHistoryDto(
                user = UserPartialMapper.entityToDto(taskHistory.user!!),
                fieldName = taskHistory.fieldName.toString().replace("_", " ").lowercase().replaceFirstChar { it.uppercase() },
                previousValue = taskHistory.previousValue,
                newValue = taskHistory.newValue,
                txDate = taskHistory.txDate,
            )
        }
    }
}