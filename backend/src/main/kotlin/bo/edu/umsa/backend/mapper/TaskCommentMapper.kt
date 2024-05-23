package bo.edu.umsa.backend.mapper

import bo.edu.umsa.backend.dto.TaskCommentDto
import bo.edu.umsa.backend.entity.TaskComment

class TaskCommentMapper {
    companion object {
        fun entityToDto(taskComment: TaskComment): TaskCommentDto {
            return TaskCommentDto(
                taskCommentId = taskComment.taskCommentId,
                user = UserPartialMapper.entityToDto(taskComment.user!!),
                taskCommentNumber = taskComment.commentNumber,
                taskComment = taskComment.comment,
                commentDate = taskComment.txDate,
                taskCommentFiles = taskComment.taskCommentFiles?.filter { it.status }?.map { FilePartialMapper.entityToDto(it.file!!) }
                    ?: emptyList(),
            )
        }
    }
}