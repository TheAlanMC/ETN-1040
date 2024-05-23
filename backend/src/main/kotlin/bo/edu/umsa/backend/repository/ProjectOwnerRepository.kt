package bo.edu.umsa.backend.repository

import bo.edu.umsa.backend.entity.ProjectOwner
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectOwnerRepository : JpaRepository<ProjectOwner, Long> {

    fun findAllByProjectIdAndStatusIsTrue(projectId: Long): List<ProjectOwner>

    fun findAllByProjectIdInAndStatusIsTrue(projectIds: List<Int>): List<ProjectOwner>

    fun findByProjectIdAndUserIdAndStatusIsTrue(
        projectId: Long,
        userId: Long
    ): ProjectOwner?

    fun findAllByUserIdAndStatusIsTrue(
        userId: Long
    ): List<ProjectOwner>

}
