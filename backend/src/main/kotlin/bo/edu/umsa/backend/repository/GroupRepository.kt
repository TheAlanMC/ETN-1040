package bo.edu.umsa.backend.repository

import bo.edu.umsa.backend.entity.Group
import bo.edu.umsa.backend.entity.Role
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
            WHERE u.username = :username
            AND g.status = true
            AND ug.status = true
            AND u.status = true
        """,
        nativeQuery = true
    )
    fun findAllByUsername(username: String): List<Group>

    fun findByGroupIdAndStatusIsTrue (groupId: Long): Group?

}
