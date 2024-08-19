package bo.edu.umsa.backend.entity

import bo.edu.umsa.backend.util.AuthUtil
import bo.edu.umsa.backend.util.HttpUtil
import jakarta.persistence.*
import java.sql.Timestamp
import java.time.LocalDate

@Entity
@Table(name = "semester", schema = "public")
class Semester {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "semester_id")
    var semesterId: Int = 0

    @Column(name = "semester_name")
    var semesterName: String = ""

    @Column(name = "semester_date_from")
    var semesterDateFrom: LocalDate = LocalDate.now()

    @Column(name = "semester_date_to")
    var semesterDateTo: LocalDate = LocalDate.now()

    @Column(name = "status")
    var status: Boolean = true

    @Column(name = "tx_date")
    var txDate: Timestamp = Timestamp(System.currentTimeMillis())

    @Column(name = "tx_user")
    var txUser: String = AuthUtil.getEmailFromAuthToken() ?: "admin"

    @Column(name = "tx_host")
    var txHost: String = HttpUtil.getRequestHost() ?: "localhost"

    @OneToMany(mappedBy = "semester", fetch = FetchType.LAZY)
    var assistant: List<Assistant>? = null

    @OneToMany(mappedBy = "semester", fetch = FetchType.LAZY)
    var assistantSchedule: List<AssistantSchedule>? = null

}

