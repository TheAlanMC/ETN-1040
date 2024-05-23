package bo.edu.umsa.backend.repository

import bo.edu.umsa.backend.entity.TaskPriority
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TaskPriorityRepository : JpaRepository<TaskPriority, Long> {

    fun findByTaskPriorityIdAndStatusIsTrue(taskPriorityId: Long): TaskPriority?

    fun findAllByStatusIsTrueOrderByTaskPriorityId(): List<TaskPriority>

}
