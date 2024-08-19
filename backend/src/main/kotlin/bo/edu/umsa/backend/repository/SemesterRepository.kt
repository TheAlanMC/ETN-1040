package bo.edu.umsa.backend.repository

import bo.edu.umsa.backend.entity.Semester
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SemesterRepository : JpaRepository<Semester, Long> {
    fun findAllByStatusIsTrueOrderBySemesterIdDesc(): List<Semester>

    fun findBySemesterIdAndStatusIsTrue(semesterId: Long): Semester?
}
