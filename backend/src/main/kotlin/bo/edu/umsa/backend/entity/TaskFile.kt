package bo.edu.umsa.backend.entity

import bo.edu.umsa.backend.util.AuthUtil
import bo.edu.umsa.backend.util.HttpUtil
import jakarta.persistence.*
import java.sql.Timestamp

@Entity
@Table(name = "task_file", schema = "public")
class TaskFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_file_id")
    var taskFileId: Int = 0

    @Column(name = "task_id")
    var taskId: Int = 0

    @Column(name = "file_id")
    var fileId: Int = 0

    @Column(name = "status")
    var status: Boolean = true

    @Column(name = "tx_date")
    var txDate: Timestamp = Timestamp(System.currentTimeMillis())

    @Column(name = "tx_user")
    var txUser: String = AuthUtil.getEmailFromAuthToken() ?: "admin"

    @Column(name = "tx_host")
    var txHost: String = HttpUtil.getRequestHost() ?: "localhost"

    @ManyToOne
    @JoinColumn(name = "task_id", insertable = false, updatable = false)
    var task: Task? = null

    @ManyToOne
    @JoinColumn(name = "file_id", insertable = false, updatable = false)
    var file: File? = null
}
