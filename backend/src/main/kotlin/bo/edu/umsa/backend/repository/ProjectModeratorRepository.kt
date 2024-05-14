package bo.edu.umsa.backend.repository

import bo.edu.umsa.backend.entity.ProjectModerator
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectModeratorRepository : JpaRepository<ProjectModerator, Long> {
    fun findAllByProjectIdAndStatusIsTrue(projectId: Long): List<ProjectModerator>
    fun findByProjectIdAndUserIdAndStatusIsTrue(projectId: Long, userId: Long): ProjectModerator?
}
