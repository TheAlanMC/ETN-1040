package bo.edu.umsa.backend.repository

import bo.edu.umsa.backend.entity.TaskFile
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TaskFileRepository : JpaRepository<TaskFile, Long> {}
