package bo.edu.umsa.backend.repository

import bo.edu.umsa.backend.entity.Schedule
import bo.edu.umsa.backend.entity.Semester
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ScheduleRepository : JpaRepository<Schedule, Long> {
    fun findAllByStatusIsTrueOrderByScheduleId(): List<Schedule>

}
