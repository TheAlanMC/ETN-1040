package bo.edu.umsa.backend.repository

import bo.edu.umsa.backend.entity.Tool
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ToolRepository : JpaRepository<Tool, Long> {

    fun findAllByStatusIsTrue(): List<Tool>

    fun findByToolIdAndStatusIsTrue(toolId: Long): Tool?
}
