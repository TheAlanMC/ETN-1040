package bo.edu.umsa.backend.repository

import bo.edu.umsa.backend.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository: JpaRepository<User, Long> {

    fun findByUsernameAndStatusIsTrue(username: String): User?
}
