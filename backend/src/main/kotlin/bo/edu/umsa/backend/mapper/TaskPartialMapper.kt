package bo.edu.umsa.backend.mapper

import bo.edu.umsa.backend.dto.TaskPartialDto
import bo.edu.umsa.backend.entity.Task

class TaskPartialMapper {
    companion object {
        fun entityToDto(task: Task): TaskPartialDto {
            return TaskPartialDto(
                projectId = task.project!!.projectId,
                taskId = task.taskId,
                taskStatus = TaskStatusMapper.entityToDto(task.taskStatus!!),
                taskPriority = TaskPriorityMapper.entityToDto(task.taskPriority!!),
                taskName = task.taskName,
                taskDescription = task.taskDescription,
                taskDueDate = task.taskDueDate,
                taskEndDate = task.taskEndDate,
                txDate = task.txDate,
                taskAssignees = task.taskAssignees?.filter { it.status }?.map { UserPartialMapper.entityToDto(it.user!!) } ?: emptyList(),
                taskFiles = task.taskFiles?.filter { it.status }?.map { FilePartialMapper.entityToDto(it.file!!) } ?: emptyList(),
            )
        }
    }
}