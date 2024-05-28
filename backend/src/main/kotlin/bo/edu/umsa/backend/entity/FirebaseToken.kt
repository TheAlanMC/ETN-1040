package bo.edu.umsa.backend.entity

import bo.edu.umsa.backend.util.AuthUtil
import bo.edu.umsa.backend.util.HttpUtil
import jakarta.persistence.*
import java.sql.Timestamp

@Entity
@Table(name = "firebase_token", schema = "public")
class FirebaseToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "firebase_token_id")
    var firebaseTokenId: Int = 0

    @Column(name = "user_id")
    var userId: Int = 0

    @Column(name = "firebase_token")
    var firebaseToken: String = ""

    @Column(name = "is_mobile")
    var isMobile: Boolean = false

    @Column(name = "status")
    var status: Boolean = true

    @Column(name = "tx_date")
    var txDate: Timestamp = Timestamp(System.currentTimeMillis())

    @Column(name = "tx_user")
    var txUser: String = AuthUtil.getEmailFromAuthToken() ?: "admin"

    @Column(name = "tx_host")
    var txHost: String = HttpUtil.getRequestHost() ?: "localhost"

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    var user: User? = null
}
