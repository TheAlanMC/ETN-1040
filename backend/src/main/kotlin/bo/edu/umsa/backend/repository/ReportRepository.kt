package bo.edu.umsa.backend.repository

import bo.edu.umsa.backend.entity.Report
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ReportRepository : JpaRepository<Report, Long> {

    fun findAll(
        specification: Specification<Report>,
        pageable: Pageable
    ): Page<Report>
}
