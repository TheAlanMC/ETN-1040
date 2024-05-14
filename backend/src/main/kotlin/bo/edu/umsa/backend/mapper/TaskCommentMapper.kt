package bo.edu.umsa.backend.mapper

import bo.edu.umsa.backend.dto.TaskCommentDto
import bo.edu.umsa.backend.entity.TaskComment

class TaskCommentMapper {
    companion object {
        fun entityToDto(taskComment: TaskComment): TaskCommentDto {
            return TaskCommentDto(
                taskCommentId = taskComment.taskCommentId,
                user = UserPartialMapper.entityToDto(taskComment.user!!),
                commentNumber = taskComment.commentNumber,
                comment = taskComment.comment,
                commentDate = taskComment.txDate,
                taskCommentFileIds = taskComment.taskCommentFiles?.filter { it.status }?.mapNotNull { it.file?.fileId }
                    ?: emptyList()
            )
        }
    }
}