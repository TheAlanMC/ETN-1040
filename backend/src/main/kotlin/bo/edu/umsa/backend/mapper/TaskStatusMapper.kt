package bo.edu.umsa.backend.mapper

import bo.edu.umsa.backend.dto.TaskStatusDto
import bo.edu.umsa.backend.entity.TaskStatus


class TaskStatusMapper {
    companion object {
        fun entityToDto(taskStatus: TaskStatus): TaskStatusDto {
            return TaskStatusDto(
                taskStatusId = taskStatus.taskStatusId,
                taskStatusName = taskStatus.taskStatusName,
            )
        }
    }
}