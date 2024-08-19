package bo.edu.umsa.backend.entity

import bo.edu.umsa.backend.util.AuthUtil
import bo.edu.umsa.backend.util.HttpUtil
import jakarta.persistence.*
import java.sql.Timestamp

@Entity
@Table(name = "role_permission", schema = "public")
class RolePermission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_permission_id")
    var rolePermissionId: Int = 0

    @Column(name = "role_id")
    var roleId: Int = 0

    @Column(name = "permission_id")
    var permissionId: Int = 0

    @Column(name = "status")
    var status: Boolean = true

    @Column(name = "tx_date")
    var txDate: Timestamp = Timestamp(System.currentTimeMillis())

    @Column(name = "tx_user")
    var txUser: String = AuthUtil.getEmailFromAuthToken() ?: "admin"

    @Column(name = "tx_host")
    var txHost: String = HttpUtil.getRequestHost() ?: "localhost"

    @ManyToOne
    @JoinColumn(name = "role_id", insertable = false, updatable = false)
    var role: Role? = null

    @ManyToOne
    @JoinColumn(name = "permission_id", insertable = false, updatable = false)
    var permission: Permission? = null
}
