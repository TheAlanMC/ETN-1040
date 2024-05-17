package bo.edu.umsa.backend.repository

import bo.edu.umsa.backend.entity.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : PagingAndSortingRepository<User, Long>,
    JpaRepository<User, Long> {

    fun findByEmailAndStatusIsTrue(email: String): User?

    fun findByUserIdAndStatusIsTrue(userId: Long): User?

    fun findAll(specification: Specification<User>, pageable: Pageable): Page<User>

    fun existsByEmailAndStatusIsTrue(email: String): Boolean

    fun findAllByStatusIsTrueOrderByUserIdAsc(): List<User>

    fun findAllByUserIdInAndStatusIsTrue(userIds: List<Int>): List<User>
}
