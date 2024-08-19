package bo.edu.umsa.backend.repository

import bo.edu.umsa.backend.entity.Assistant
import bo.edu.umsa.backend.entity.Semester
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface AssistantRepository : JpaRepository<Assistant, Long> {
    fun findAllBySemesterIdAndStatusIsTrue (semesterId: Long): List<Assistant>

    fun findByAssistantIdAndStatusIsTrue (assistantId: Long): Assistant?

    fun findAllByAssistantIdInAndStatusIsTrue (assistantIds: List<Int>): List<Assistant>
}
