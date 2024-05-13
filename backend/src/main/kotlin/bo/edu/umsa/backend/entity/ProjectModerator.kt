package bo.edu.umsa.backend.entity

import bo.edu.umsa.backend.util.AuthUtil
import bo.edu.umsa.backend.util.HttpUtil
import jakarta.persistence.*
import java.sql.Timestamp

@Entity
@Table(name = "project_moderator", schema = "public")
class ProjectModerator {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_moderator_id")
    var projectModeratorId: Int = 0

    @Column(name = "project_id")
    var projectId: Int = 0

    @Column(name = "user_id")
    var userId: Int = 0

    @Column(name = "status")
    var status: Boolean = true

    @Column(name = "tx_date")
    var txDate: Timestamp = Timestamp(System.currentTimeMillis())

    @Column(name = "tx_user")
    var txUser: String = AuthUtil.getEmailFromAuthToken() ?: "admin"

    @Column(name = "tx_host")
    var txHost: String = HttpUtil.getRequestHost() ?: "localhost"

    @ManyToOne
    @JoinColumn(name = "project_id", insertable = false, updatable = false)
    var project: Project? = null

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    var user: User? = null
}
