package bo.edu.umsa.backend.repository

import bo.edu.umsa.backend.entity.GroupRole
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GroupRoleRepository : JpaRepository<GroupRole, Long> {

    fun findAllByGroupIdAndStatusIsTrue(groupId: Long): List<GroupRole>

}
