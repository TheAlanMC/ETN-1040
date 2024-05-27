package bo.edu.umsa.backend.repository

import bo.edu.umsa.backend.entity.Task
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import java.sql.Timestamp

@Repository
interface TaskRepository : PagingAndSortingRepository<Task, Long>,
    JpaRepository<Task, Long> {

    fun findAll(
        specification: Specification<Task>,
        pageable: Pageable
    ): Page<Task>

    fun findByTaskIdAndStatusIsTrue(taskId: Long): Task?

    fun findAllByProjectIdAndStatusIsTrue(projectId: Long): List<Task>

    fun findAllByTaskDueDateBetweenAndTaskEndDateIsNullAndStatusIsTrueOrderByTaskDueDate(
        startDate: Timestamp,
        endDate: Timestamp
    ): List<Task>

    @Query("""
        SELECT *
        FROM task t
        WHERE (t.tx_date BETWEEN :startDate AND :endDate)
        OR (t.task_due_date BETWEEN :startDate AND :endDate)
        OR (t.task_end_date BETWEEN :startDate AND :endDate)
        AND t.status = true
        ORDER BY t.tx_date DESC
    """, nativeQuery = true)
    fun findAllTasksByDateRange(
        startDate: Timestamp,
        endDate: Timestamp,
    ): List<Task>
}