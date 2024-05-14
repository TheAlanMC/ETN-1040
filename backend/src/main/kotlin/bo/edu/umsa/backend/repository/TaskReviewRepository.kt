package bo.edu.umsa.backend.repository

import bo.edu.umsa.backend.entity.TaskReview
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TaskReviewRepository : JpaRepository<TaskReview, Long> {}
