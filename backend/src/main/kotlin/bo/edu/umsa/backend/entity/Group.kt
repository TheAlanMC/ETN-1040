package bo.edu.umsa.backend.entity

import bo.edu.umsa.backend.util.AuthUtil
import bo.edu.umsa.backend.util.HttpUtil
import jakarta.persistence.*
import java.sql.Timestamp

@Entity
@Table(name = "group", schema = "public")
class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    var groupId: Int = 0

    @Column(name = "group_name")
    var groupName: String = ""

    @Column(name = "group_description")
    var groupDescription: String = ""

    @Column(name = "status")
    var status: Boolean = true

    @Column(name = "tx_date")
    var txDate: Timestamp = Timestamp(System.currentTimeMillis())

    @Column(name = "tx_user")
    var txUser: String = AuthUtil.getEmailFromAuthToken() ?: "admin"

    @Column(name = "tx_host")
    var txHost: String = HttpUtil.getRequestHost() ?: "localhost"

    @OneToMany(mappedBy = "group", fetch = FetchType.LAZY)
    var userGroups: List<UserGroup>? = null

    @OneToMany(mappedBy = "group", fetch = FetchType.LAZY)
    var groupRoles: List<GroupRole>? = null


}

