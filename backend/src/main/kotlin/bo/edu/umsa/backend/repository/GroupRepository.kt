package bo.edu.umsa.backend.repository

import bo.edu.umsa.backend.entity.Group
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface GroupRepository: JpaRepository<Group, Long> {

    @Query(
        """
            SELECT g.* FROM "group" g
            JOIN user_group ug ON g.group_id = ug.group_id
            JOIN "user" u ON ug.user_id = u.user_id
            WHERE u.email = :email
            AND g.status = true
            AND ug.status = true
            AND u.status = true
            ORDER BY g.group_id
        """,
        nativeQuery = true
    )
    fun findAllByEmail(email: String): List<Group>

    fun findByGroupIdAndStatusIsTrue (groupId: Long): Group?

    fun findAllByStatusIsTrueOrderByGroupId (): List<Group>

    @Query(
        """
            SELECT g.* FROM "group" g
            WHERE g.group_id IN :groupIds
            AND g.status = true
            ORDER BY g.group_id
        """,
        nativeQuery = true
    )
    fun findAllByGroupIds (groupIds: List<Long>): List<Group>

    @Query(
        """
            SELECT g.* FROM "group" g
            JOIN user_group ug ON g.group_id = ug.group_id
            WHERE ug.user_id = :userId
            AND g.status = true
            AND ug.status = true
            ORDER BY g.group_id
        """,
        nativeQuery = true
    )
    fun findAllByUserId (userId: Long): List<Group>
}
