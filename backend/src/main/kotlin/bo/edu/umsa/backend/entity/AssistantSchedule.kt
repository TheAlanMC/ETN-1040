package bo.edu.umsa.backend.entity

import bo.edu.umsa.backend.util.AuthUtil
import bo.edu.umsa.backend.util.HttpUtil
import jakarta.persistence.*
import java.sql.Timestamp

@Entity
@Table(name = "assistant_schedule", schema = "public")
class AssistantSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "assistant_schedule_id")
    var assistantScheduleId: Int = 0

    @Column(name = "assistant_id")
    var assistantId: Int = 0

    @Column(name = "schedule_id")
    var scheduleId: Int = 0

    @Column(name = "semester_id")
    var semesterId: Int = 0

    @Column(name = "status")
    var status: Boolean = true

    @Column(name = "tx_date")
    var txDate: Timestamp = Timestamp(System.currentTimeMillis())

    @Column(name = "tx_user")
    var txUser: String = AuthUtil.getEmailFromAuthToken() ?: "admin"

    @Column(name = "tx_host")
    var txHost: String = HttpUtil.getRequestHost() ?: "localhost"

    @ManyToOne
    @JoinColumn(name = "assistant_id", insertable = false, updatable = false)
    var assistant: Assistant? = null

    @ManyToOne
    @JoinColumn(name = "schedule_id", insertable = false, updatable = false)
    var schedule: Schedule? = null

    @ManyToOne
    @JoinColumn(name = "semester_id", insertable = false, updatable = false)
    var semester: Semester? = null
}


