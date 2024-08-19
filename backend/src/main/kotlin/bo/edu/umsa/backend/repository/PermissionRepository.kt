package bo.edu.umsa.backend.repository

import bo.edu.umsa.backend.entity.Permission
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface PermissionRepository : JpaRepository<Permission, Long> {

    @Query("""
            SELECT p.* FROM permission p
            JOIN role_permission rp ON p.permission_id = rp.permission_id
            JOIN "role" r ON rp.role_id = r.role_id
            JOIN user_role ur ON r.role_id = ur.role_id
            JOIN "user" u ON ur.user_id = u.user_id
            WHERE u.email = :email
            AND p.status = true
            AND rp.status = true
            AND r.status = true
            AND ur.status = true
            AND u.status = true
            ORDER BY r.role_id
        """, nativeQuery = true)
    fun findAllByEmail(email: String): List<Permission>

    @Query("""
            SELECT p.* FROM permission p
            JOIN role_permission rp ON p.permission_id = rp.permission_id
            JOIN "role" r ON rp.role_id = r.role_id
            WHERE r.role_id = :roleId
            AND p.status = true
            AND rp.status = true
            AND r.status = true
            ORDER BY r.role_id
        """, nativeQuery = true)
    fun findAllByRoleId(roleId: Long): List<Permission>

    @Query("""
            SELECT p.* FROM permission p
            WHERE p.permission_id IN :permissionIds
            AND p.status = true
            ORDER BY p.permission_id
        """, nativeQuery = true)
    fun findAllByPermissionIds(permissionIds: List<Long>): List<Permission>

    fun findAllByStatusIsTrueOrderByPermissionId(): List<Permission>

}
