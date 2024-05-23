package bo.edu.umsa.backend.repository

import bo.edu.umsa.backend.entity.ReplacedPartFile
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ReplacedPartFileRepository : JpaRepository<ReplacedPartFile, Long> {

    fun findByReplacedPartFileIdAndStatusIsTrue(replacedPartFileId: Long): ReplacedPartFile?

    fun findAllByReplacedPartIdAndStatusIsTrue(replacedPartId: Long): List<ReplacedPartFile>
}
