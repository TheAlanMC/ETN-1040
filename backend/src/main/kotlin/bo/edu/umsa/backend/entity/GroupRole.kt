package bo.edu.umsa.backend.entity

import bo.edu.umsa.backend.util.AuthUtil
import bo.edu.umsa.backend.util.HttpUtil
import jakarta.persistence.*
import java.sql.Timestamp

@Entity
@Table(name = "group_role", schema = "public")
class GroupRole {
    @Id
    @Column(name = "group_role_id")
    var groupRoleId: Int = 0

    @Column(name = "group_id")
    var groupId: Int = 0

    @Column(name = "role_id")
    var roleId: Int = 0

    @Column(name = "status")
    var status: Boolean = true

    @Column(name = "tx_date")
    var txDate: Timestamp = Timestamp(System.currentTimeMillis())

    @Column(name = "tx_user")
    var txUser: String = AuthUtil.getUsernameFromAuthToken() ?: "admin"

    @Column(name = "tx_host")
    var txHost: String = HttpUtil.getRequestHost() ?: "localhost"

    @ManyToOne
    @JoinColumn(name = "group_id", insertable = false, updatable = false)
    var group: Group? = null

    @ManyToOne
    @JoinColumn(name = "role_id", insertable = false, updatable = false)
    var role: Role? = null
}
