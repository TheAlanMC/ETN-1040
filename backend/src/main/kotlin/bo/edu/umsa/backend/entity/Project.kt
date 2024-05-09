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

    @Column(name = "date_from")
    var dateFrom: Timestamp = Timestamp(System.currentTimeMillis())

    @Column(name = "date_to")
    var dateTo: Timestamp = Timestamp(System.currentTimeMillis())

    @Column(name = "status")
    var status: Boolean = true

    @Column(name = "tx_date")
    var txDate: Timestamp = Timestamp(System.currentTimeMillis())

    @Column(name = "tx_user")
    var txUser: String = AuthUtil.getUsernameFromAuthToken() ?: "admin"

    @Column(name = "tx_host")
    var txHost: String = HttpUtil.getRequestHost() ?: "localhost"
}
