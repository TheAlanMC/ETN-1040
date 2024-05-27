package bo.edu.umsa.backend.entity

import bo.edu.umsa.backend.dto.ReportType
import bo.edu.umsa.backend.util.AuthUtil
import bo.edu.umsa.backend.util.HttpUtil
import jakarta.persistence.*
import java.sql.Timestamp

@Entity
@Table(name = "report")
class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    var reportId: Int = 0

    @Column(name = "user_id")
    var userId: Int = 0

    @Column(name = "file_id")
    var fileId: Int = 0

    @Column(name = "report_start_date")
    var reportStartDate: Timestamp = Timestamp(System.currentTimeMillis())

    @Column(name = "report_end_date")
    var reportEndDate: Timestamp = Timestamp(System.currentTimeMillis())

    @Enumerated(EnumType.STRING)
    @Column(name = "report_type")
    var reportType: ReportType = ReportType.TAREA

    @Column(name = "report_name")
    var reportName: String = ""

    @Column(name = "status")
    var status: Boolean = true

    @Column(name = "tx_date")
    var txDate: Timestamp = Timestamp(System.currentTimeMillis())

    @Column(name = "tx_user")
    var txUser: String = AuthUtil.getEmailFromAuthToken() ?: "admin"

    @Column(name = "tx_host")
    var txHost: String = HttpUtil.getRequestHost() ?: "localhost"

    @ManyToOne
    @JoinColumn(name = "file_id", insertable = false, updatable = false)
    var file: File? = null

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    var user: User? = null

}
