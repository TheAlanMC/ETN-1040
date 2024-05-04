package bo.edu.umsa.backend.repository

import bo.edu.umsa.backend.entity.UserGroup
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserGroupRepository: JpaRepository<UserGroup, Long> {

    fun findAllByGroupIdAndStatusIsTrue (groupId: Long): List<UserGroup>

    fun findAllByUserIdAndStatusIsTrue (userId: Long): List<UserGroup>

}
