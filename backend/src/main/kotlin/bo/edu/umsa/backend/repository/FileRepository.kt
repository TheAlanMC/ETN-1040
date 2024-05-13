package bo.edu.umsa.backend.repository

import bo.edu.umsa.backend.entity.File
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FileRepository : JpaRepository<File, Long> {

    fun findByFileIdAndStatusIsTrue(fileId: Long): File?
}
