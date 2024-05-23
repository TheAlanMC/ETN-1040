package bo.edu.umsa.backend.repository

import bo.edu.umsa.backend.entity.FirebaseToken
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FirebaseTokenRepository : JpaRepository<FirebaseToken, Long> {
    fun findByFirebaseTokenAndStatusIsTrue(firebaseToken: String): FirebaseToken?

    fun findAllByUserIdAndStatusIsTrue(userId: Long): List<FirebaseToken>
}
