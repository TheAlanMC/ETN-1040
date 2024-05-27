package bo.edu.umsa.backend.repository

import bo.edu.umsa.backend.entity.Project
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.sql.Timestamp

@Repository
interface ProjectRepository : JpaRepository<Project, Long> {

    fun findAll(
        specification: Specification<Project>,
        pageable: Pageable
    ): Page<Project>

    fun findByProjectIdAndStatusIsTrue(projectId: Long): Project?

    fun findAllByProjectIdInAndProjectEndDateIsNullAndStatusIsTrueOrderByProjectIdDesc(projectIds: List<Int>): List<Project>

    fun findAllByProjectDateToBetweenAndProjectEndDateIsNullAndStatusIsTrueOrderByProjectDateTo(
        startDate: Timestamp,
        endDate: Timestamp
    ): List<Project>

    @Query("""
        SELECT *
        FROM project p 
        WHERE (p.project_date_from BETWEEN :startDate AND :endDate)
        OR (p.project_date_to BETWEEN :startDate AND :endDate)
        OR (p.project_end_date BETWEEN :startDate AND :endDate)
        AND p.status = true
        ORDER BY p.tx_date DESC
    """, nativeQuery = true)
    fun findAllProjectsByDateRange(
        startDate: Timestamp,
        endDate: Timestamp
    ): List<Project>

}
