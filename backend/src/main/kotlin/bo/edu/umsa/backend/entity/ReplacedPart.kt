package bo.edu.umsa.backend.entity

import bo.edu.umsa.backend.util.AuthUtil
import bo.edu.umsa.backend.util.HttpUtil
import jakarta.persistence.*
import java.sql.Timestamp

@Entity
@Table(name = "replaced_part", schema = "public")
class ReplacedPart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "replaced_part_id")
    var replacedPartId: Int = 0

    @Column(name = "task_id")
    var taskId: Int = 0

    @Column(name = "replaced_part_description")
    var replacedPartDescription: String = ""

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

    @OneToMany(mappedBy = "replacedPart")
    var replacedPartFiles: List<ReplacedPartFile>? = null
}
