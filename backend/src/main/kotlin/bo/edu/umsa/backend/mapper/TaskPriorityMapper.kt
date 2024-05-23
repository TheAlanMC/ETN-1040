package bo.edu.umsa.backend.mapper

import bo.edu.umsa.backend.dto.TaskPriorityDto
import bo.edu.umsa.backend.entity.TaskPriority


class TaskPriorityMapper {
    companion object {
        fun entityToDto(taskPriority: TaskPriority): TaskPriorityDto {
            return TaskPriorityDto(
                taskPriorityId = taskPriority.taskPriorityId,
                taskPriorityName = taskPriority.taskPriorityName,
            )
        }
    }
}