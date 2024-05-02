package bo.edu.umsa.backend.entity

import bo.edu.umsa.backend.util.AuthUtil
import bo.edu.umsa.backend.util.HttpUtil
import jakarta.persistence.*
import java.sql.Timestamp

@Entity
@Table(name = "account_recovery", schema = "public")
class AccountRecovery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_recovery_id")
    var accountRecoveryId: Int = 0

    @Column(name = "user_id")
    var userId: Int = 0

    @Column(name = "hash_code")
    var hashCode: String = ""

    @Column(name = "expiration_date")
    var expirationDate: Timestamp = Timestamp(System.currentTimeMillis())

    @Column(name = "status")
    var status: Boolean = true

    @Column(name = "tx_date")
    var txDate: Timestamp = Timestamp(System.currentTimeMillis())

    @Column(name = "tx_user")
    var txUser: String = AuthUtil.getUsernameFromAuthToken() ?: "admin"

    @Column(name = "tx_host")
    var txHost: String = HttpUtil.getRequestHost() ?: "localhost"

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    var user: User? = null
}

