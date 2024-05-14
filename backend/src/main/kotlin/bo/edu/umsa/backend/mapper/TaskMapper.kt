package bo.edu.umsa.backend.mapper

import bo.edu.umsa.backend.dto.TaskDto
import bo.edu.umsa.backend.entity.Task

class TaskMapper {
    companion object {
        fun entityToDto(task: Task): TaskDto {
            return TaskDto(
                taskId = task.taskId,
                taskStatusId = task.taskStatus?.taskStatusId ?: 0,
                taskStatus = task.taskStatus?.taskStatusName ?: "",
                taskName = task.taskName,
                taskDescription = task.taskDescription,
                taskDeadline = task.taskDeadline,
                taskPriority = task.taskPriority,
                taskAssigneeIds = task.taskAssignees?.filter { it.status }?.mapNotNull { it.user?.userId }
                    ?: emptyList(),
            )
        }
    }
}