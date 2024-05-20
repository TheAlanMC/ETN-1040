package bo.edu.umsa.backend.entity

import bo.edu.umsa.backend.dto.TaskHistoryType
import bo.edu.umsa.backend.util.AuthUtil
import bo.edu.umsa.backend.util.HttpUtil
import jakarta.persistence.*
import java.sql.Timestamp


@Entity
@Table(name = "task_history", schema = "public")
class TaskHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_history_id")
    var taskHistoryId: Int = 0

    @Column(name = "task_id")
    var taskId: Int = 0

    @Column(name = "user_id")
    var userId: Int = 0

    @Enumerated(EnumType.STRING)
    @Column(name = "field_name")
    var fieldName: TaskHistoryType = TaskHistoryType.TAREA

    @Column(name = "previous_value")
    var previousValue: String = ""

    @Column(name = "new_value")
    var newValue: String = ""

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
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    var user: User? = null
}
