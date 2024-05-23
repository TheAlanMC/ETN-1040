package bo.edu.umsa.backend.entity

import bo.edu.umsa.backend.util.AuthUtil
import bo.edu.umsa.backend.util.HttpUtil
import jakarta.persistence.*
import java.sql.Timestamp

@Entity
@Table(name = "project", schema = "public")
class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    var projectId: Int = 0

    @Column(name = "project_name")
    var projectName: String = ""

    @Column(name = "project_description", columnDefinition = "TEXT")
    var projectDescription: String = ""

    @Column(name = "project_objective")
    var projectObjective: String = ""

    @Column(name = "project_close_message")
    var projectCloseMessage: String = ""

    @Column(name = "project_date_from")
    var projectDateFrom: Timestamp = Timestamp(System.currentTimeMillis())

    @Column(name = "project_date_to")
    var projectDateTo: Timestamp = Timestamp(System.currentTimeMillis())

    @Column(name = "project_end_date")
    var projectEndDate: Timestamp = Timestamp(System.currentTimeMillis())

    @Column(name = "status")
    var status: Boolean = true

    @Column(name = "tx_date")
    var txDate: Timestamp = Timestamp(System.currentTimeMillis())

    @Column(name = "tx_user")
    var txUser: String = AuthUtil.getEmailFromAuthToken() ?: "admin"

    @Column(name = "tx_host")
    var txHost: String = HttpUtil.getRequestHost() ?: "localhost"

    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
    var projectOwners: List<ProjectOwner>? = null

    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
    var projectModerators: List<ProjectModerator>? = null

    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
    var projectMembers: List<ProjectMember>? = null
}
