package bo.edu.umsa.backend.repository

import bo.edu.umsa.backend.entity.TaskComment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TaskCommentRepository : JpaRepository<TaskComment, Long> {

    fun findByTaskCommentIdAndStatusIsTrue(taskCommentId: Long): TaskComment?

    fun findFirstByTaskIdOrderByCommentNumberDesc(taskId: Long): TaskComment?
}
