package bo.edu.umsa.backend.repository

import bo.edu.umsa.backend.entity.Group
import bo.edu.umsa.backend.entity.Role
import bo.edu.umsa.backend.entity.UserGroup
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface UserGroupRepository: JpaRepository<UserGroup, Long> {

}
