package bo.edu.umsa.backend.repository

import bo.edu.umsa.backend.entity.Task
import bo.edu.umsa.backend.entity.TaskHistory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TaskHistoryRepository : JpaRepository<TaskHistory, Long> {

    fun findAllByTaskIdAndStatusIsTrueOrderByTaskHistoryIdDesc(taskId: Long): List<TaskHistory>
}
