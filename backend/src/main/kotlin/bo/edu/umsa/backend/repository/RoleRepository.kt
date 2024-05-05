package bo.edu.umsa.backend.repository

import bo.edu.umsa.backend.entity.Role
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface RoleRepository: JpaRepository<Role, Long> {

    @Query(
        """
            SELECT r.* FROM role r
            JOIN group_role gr ON r.role_id = gr.role_id
            JOIN "group" g ON gr.group_id = g.group_id
            JOIN user_group ug ON g.group_id = ug.group_id
            JOIN "user" u ON ug.user_id = u.user_id
            WHERE u.username = :username
            AND r.status = true
            AND gr.status = true
            AND g.status = true
            AND ug.status = true
            AND u.status = true
        """,
        nativeQuery = true
    )
    fun findAllByUsername(username: String): List<Role>

    @Query(
        """
            SELECT r.* FROM role r
            JOIN group_role gr ON r.role_id = gr.role_id
            JOIN "group" g ON gr.group_id = g.group_id
            WHERE g.group_id = :groupId
            AND r.status = true
            AND gr.status = true
            AND g.status = true
            ORDER BY r.role_id
        """,
        nativeQuery = true
    )
    fun findAllByGroupId(groupId: Long): List<Role>

    @Query(
        """
            SELECT r.* FROM role r
            WHERE r.role_id IN :roleIds
            AND r.status = true
            ORDER BY r.role_id
        """,
        nativeQuery = true
    )
    fun findAllByRoleIds (roleIds: List<Long>): List<Role>

    fun findAllByStatusIsTrueOrderByRoleId (): List<Role>

}
