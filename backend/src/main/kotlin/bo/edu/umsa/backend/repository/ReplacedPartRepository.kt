package bo.edu.umsa.backend.repository

import bo.edu.umsa.backend.entity.ReplacedPart
import bo.edu.umsa.backend.entity.TaskComment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ReplacedPartRepository : JpaRepository<ReplacedPart, Long> {

    fun findByReplacedPartIdAndStatusIsTrue(replacedPartId: Long): ReplacedPart?
}
