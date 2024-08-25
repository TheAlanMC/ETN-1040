package bo.edu.umsa.backend.repository

import bo.edu.umsa.backend.entity.AssistantSchedule
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AssistantScheduleRepository : JpaRepository<AssistantSchedule, Long> {
    fun findAllBySemesterIdAndStatusIsTrue (semesterId: Long): List<AssistantSchedule>

    fun findAllByAssistantIdAndStatusIsTrue (assistantId: Long): List<AssistantSchedule>

    fun findAllBySemesterIdAndScheduleIdInAndStatusIsTrue(
        semesterId: Long,
        scheduleIds: List<Long>
    ): List<AssistantSchedule>
}
