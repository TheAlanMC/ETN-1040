package bo.edu.umsa.backend.repository

import bo.edu.umsa.backend.entity.Role
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface RoleRepository : JpaRepository<Role, Long> {

    @Query("""
            SELECT r.* FROM "role" r
            JOIN user_role ur ON r.role_id = ur.role_id
            JOIN "user" u ON ur.user_id = u.user_id
            WHERE u.email = :email
            AND r.status = true
            AND ur.status = true
            AND u.status = true
            ORDER BY r.role_id
        """, nativeQuery = true)
    fun findAllByEmail(email: String): List<Role>

    fun findByRoleIdAndStatusIsTrue(roleId: Long): Role?

    fun findAllByStatusIsTrueOrderByRoleId(): List<Role>

    @Query("""
            SELECT r.* FROM "role" r
            WHERE r.role_id IN :roleIds
            AND r.status = true
            ORDER BY r.role_id
        """, nativeQuery = true)
    fun findAllByRoleIds(roleIds: List<Long>): List<Role>

    @Query("""
            SELECT r.* FROM "role" r
            JOIN user_role ur ON r.role_id = ur.role_id
            WHERE ur.user_id = :userId
            AND r.status = true
            AND ur.status = true
            ORDER BY r.role_id
        """, nativeQuery = true)
    fun findAllByUserId(userId: Long): List<Role>
}
