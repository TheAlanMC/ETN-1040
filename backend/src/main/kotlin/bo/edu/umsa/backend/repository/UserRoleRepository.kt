package bo.edu.umsa.backend.repository

import bo.edu.umsa.backend.entity.UserRole
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRoleRepository : JpaRepository<UserRole, Long> {

    fun findAllByRoleIdAndStatusIsTrue(roleId: Long): List<UserRole>

    fun findAllByUserIdAndStatusIsTrue(userId: Long): List<UserRole>

}
