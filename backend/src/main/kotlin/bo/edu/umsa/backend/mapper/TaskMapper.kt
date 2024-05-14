package bo.edu.umsa.backend.mapper

import bo.edu.umsa.backend.dto.TaskDto
import bo.edu.umsa.backend.entity.Task

class TaskMapper {
    companion object {
        fun entityToDto(task: Task): TaskDto {
            return TaskDto(
                taskId = task.taskId,
                taskStatus = TaskStatusMapper.entityToDto(task.taskStatus!!),
                project = ProjectPartialMapper.entityToDto(task.project!!),
                taskName = task.taskName,
                taskDescription = task.taskDescription,
                taskCreationDate = task.txDate,
                taskDeadline = task.taskDeadline,
                createdBy = task.txUser,
                taskPriority = task.taskPriority,
                taskAssigneeIds = task.taskAssignees?.filter { it.status }?.mapNotNull { it.user?.userId }
                    ?: emptyList(),
                taskFileIds = task.taskFiles?.filter { it.status }?.mapNotNull { it.file?.fileId } ?: emptyList()
            )
        }
    }
}