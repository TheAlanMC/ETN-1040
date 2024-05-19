package bo.edu.umsa.backend.repository

import bo.edu.umsa.backend.entity.TaskAssignee
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TaskAssigneeRepository : JpaRepository<TaskAssignee, Long> {

    fun findAllByTaskIdAndStatusIsTrue(taskId: Long): List<TaskAssignee>

    fun findByTaskIdAndUserIdAndStatusIsTrue(
        taskId: Long,
        userId: Long
    ): TaskAssignee?

    fun findAllByTaskIdInAndStatusIsTrue(taskIds: List<Int>): List<TaskAssignee>
}
