package bo.edu.umsa.backend.repository

import bo.edu.umsa.backend.entity.Project
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectRepository : JpaRepository<Project, Long> {

    fun findAll(
        specification: Specification<Project>,
        pageable: Pageable
    ): Page<Project>

    fun findByProjectIdAndStatusIsTrue(projectId: Long): Project?

    fun findAllByStatusIsTrueOrderByProjectIdDesc(): List<Project>

}
