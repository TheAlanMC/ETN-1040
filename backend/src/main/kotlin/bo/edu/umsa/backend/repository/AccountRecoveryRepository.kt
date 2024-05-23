package bo.edu.umsa.backend.repository

import bo.edu.umsa.backend.entity.AccountRecovery
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AccountRecoveryRepository : JpaRepository<AccountRecovery, Long> {

    fun findAllByUserEmailAndStatusIsTrueAndStatusIsTrue(email: String): List<AccountRecovery>

}
