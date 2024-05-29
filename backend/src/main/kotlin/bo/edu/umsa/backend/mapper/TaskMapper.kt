package bo.edu.umsa.backend.mapper

import bo.edu.umsa.backend.dto.TaskDto
import bo.edu.umsa.backend.entity.Task

class TaskMapper {
    companion object {
        fun entityToDto(task: Task): TaskDto {
            return TaskDto(
                taskId = task.taskId,
                project = ProjectPartialMapper.entityToDto(task.project!!),
                taskStatus = TaskStatusMapper.entityToDto(task.taskStatus!!),
                taskPriority = TaskPriorityMapper.entityToDto(task.taskPriority!!),
                taskName = task.taskName,
                taskDescription = task.taskDescription,
                taskDueDate = task.taskDueDate,
                taskEndDate = task.taskEndDate,
                taskRating = task.taskRating,
                taskRatingComment = task.taskRatingComment,
                txDate = task.txDate,
                txHost = task.txHost,
                taskAssignees = task.taskAssignees?.filter { it.status && it.user?.status == true }?.map { UserPartialMapper.entityToDto(it.user!!) }
                    ?: emptyList(),
                taskFiles = task.taskFiles?.filter { it.status }?.map { FilePartialMapper.entityToDto(it.file!!) } ?: emptyList(),
                taskComments = task.taskComments?.filter { it.status }?.sortedByDescending { it.taskCommentNumber }?.map { TaskCommentMapper.entityToDto(it) }
                    ?: emptyList(),
                replacedParts = task.replacedParts?.filter { it.status }?.map { ReplacedPartMapper.entityToDto(it) } ?: emptyList(),
            )
        }
    }
}