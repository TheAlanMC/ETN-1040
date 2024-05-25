package bo.edu.umsa.backend.repository

import bo.edu.umsa.backend.entity.File
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface FileRepository : JpaRepository<File, Long> {

    fun findByFileIdAndStatusIsTrue(fileId: Long): File?

    @Query(
        """
            SELECT f.fileId
            FROM File f
            WHERE f.fileId IN :fileIds
            AND f.status = true
        """
    )
    fun findAllByFileIdInAndStatusIsTrue(fileIds: List<Int>): List<Int>
}
