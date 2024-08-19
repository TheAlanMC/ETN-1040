package bo.edu.umsa.backend.repository

import bo.edu.umsa.backend.entity.RolePermission
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RolePermissionRepository : JpaRepository<RolePermission, Long> {

    fun findAllByRoleIdAndStatusIsTrue(roleId: Long): List<RolePermission>

}
