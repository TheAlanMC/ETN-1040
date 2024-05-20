package bo.edu.umsa.backend.mapper

import bo.edu.umsa.backend.dto.TaskHistoryDto
import bo.edu.umsa.backend.entity.TaskHistory

class TaskHistoryMapper {
    companion object {
        fun entityToDto(taskHistory: TaskHistory): TaskHistoryDto {
            return TaskHistoryDto(
                createdDate = taskHistory.txDate,
                createdBy = taskHistory.user.let { UserPartialMapper.entityToDto(it!!) },
                fieldName = taskHistory.fieldName.toString().replace("_", " ").lowercase().replaceFirstChar { it.uppercase() },
                previousValue = taskHistory.previousValue,
                newValue = taskHistory.newValue,
            )
        }
    }
}