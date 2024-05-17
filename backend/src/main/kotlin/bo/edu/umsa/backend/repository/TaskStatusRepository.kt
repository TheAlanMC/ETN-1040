package bo.edu.umsa.backend.repository

import bo.edu.umsa.backend.entity.TaskStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TaskStatusRepository : JpaRepository<TaskStatus, Long> {

    fun findByTaskStatusIdAndStatusIsTrue(taskStatusId: Long): TaskStatus?

    fun findAllByStatusIsTrueOrderByTaskStatusId(): List<TaskStatus>
}
