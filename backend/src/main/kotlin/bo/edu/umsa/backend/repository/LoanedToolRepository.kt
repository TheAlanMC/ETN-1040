package bo.edu.umsa.backend.repository

import bo.edu.umsa.backend.entity.LoanedTool
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LoanedToolRepository : JpaRepository<LoanedTool, Long> {
    fun findByToolIdAndStatusIsTrue(toolId: Long): LoanedTool?
}
