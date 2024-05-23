package bo.edu.umsa.backend.repository

import bo.edu.umsa.backend.entity.ProjectMember
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectMemberRepository : JpaRepository<ProjectMember, Long> {
    fun findAllByProjectIdAndStatusIsTrue(projectId: Long): List<ProjectMember>

    fun findAllByProjectIdInAndStatusIsTrue(projectIds: List<Int>): List<ProjectMember>

    fun findAllByProjectIdAndUserIdInAndStatusIsTrue(
        projectId: Long,
        userIds: List<Int>
    ): List<ProjectMember>

    fun findByProjectIdAndUserIdAndStatusIsTrue(
        projectId: Long,
        userId: Long
    ): ProjectMember?
}
