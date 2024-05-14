package bo.edu.umsa.backend.repository

import bo.edu.umsa.backend.entity.TaskCommentFile
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TaskCommentFileRepository : JpaRepository<TaskCommentFile, Long> {}
