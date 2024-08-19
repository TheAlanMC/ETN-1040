package bo.edu.umsa.backend.entity

import bo.edu.umsa.backend.util.AuthUtil
import bo.edu.umsa.backend.util.HttpUtil
import jakarta.persistence.*
import java.sql.Timestamp
import java.time.LocalTime

@Entity
@Table(name = "schedule", schema = "public")
class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    var scheduleId: Int = 0

    @Column(name = "day_of_week")
    var dayOfWeek: String = ""

    @Column(name = "day_number")
    var dayNumber: Int = 0

    @Column(name = "hour_from")
    var hourFrom: LocalTime = LocalTime.now()

    @Column(name = "hour_to")
    var hourTo: LocalTime = LocalTime.now()

    @Column(name = "status")
    var status: Boolean = true

    @Column(name = "tx_date")
    var txDate: Timestamp = Timestamp(System.currentTimeMillis())

    @Column(name = "tx_user")
    var txUser: String = AuthUtil.getEmailFromAuthToken() ?: "admin"

    @Column(name = "tx_host")
    var txHost: String = HttpUtil.getRequestHost() ?: "localhost"

    @OneToMany(mappedBy = "schedule", fetch = FetchType.LAZY)
    var assistantSchedules: List<AssistantSchedule>? = null

}

