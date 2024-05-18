package bo.edu.umsa.backend.mapper

import bo.edu.umsa.backend.dto.TaskDto
import bo.edu.umsa.backend.dto.TaskPartialDto
import bo.edu.umsa.backend.entity.Task

class TaskPartialMapper {
    companion object {
        fun entityToDto(task: Task): TaskPartialDto {
            return TaskPartialDto(
                taskId = task.taskId,
                taskStatus = TaskStatusMapper.entityToDto(task.taskStatus!!),
                taskName = task.taskName,
                taskDescription = task.taskDescription,
                taskCreationDate = task.txDate,
                taskDeadline = task.taskDeadline,
                taskPriority = task.taskPriority,
                taskAssignees = task.taskAssignees?.filter { it.status }?.map { UserPartialMapper.entityToDto(it.user!!) } ?: emptyList(),
                taskFiles = task.taskFiles?.filter { it.status }?.map { FilePartialMapper.entityToDto(it.file!!) } ?: emptyList()
            )
        }
    }
}