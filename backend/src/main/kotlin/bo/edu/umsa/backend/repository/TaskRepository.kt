package bo.edu.umsa.backend.repository

import bo.edu.umsa.backend.entity.Task
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface TaskRepository : PagingAndSortingRepository<Task, Long>,
    JpaRepository<Task, Long> {

    fun findAll(
        specification: Specification<Task>,
        pageable: Pageable
    ): Page<Task>

    fun findByTaskIdAndStatusIsTrue(taskId: Long): Task?

    fun findAllByProjectIdAndStatusIsTrue(projectId: Long): List<Task>

}
