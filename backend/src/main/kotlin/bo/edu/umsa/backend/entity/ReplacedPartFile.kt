package bo.edu.umsa.backend.entity

import bo.edu.umsa.backend.util.AuthUtil
import bo.edu.umsa.backend.util.HttpUtil
import jakarta.persistence.*
import java.sql.Timestamp

@Entity
@Table(name = "replaced_part_file", schema = "public")
class ReplacedPartFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "replaced_part_file_id")
    var replacedPartFileId: Int = 0

    @Column(name = "replaced_part_id")
    var replacedPartId: Int = 0

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
    @JoinColumn(name = "replaced_part_id", insertable = false, updatable = false)
    var replacedPart: ReplacedPart? = null

    @ManyToOne
    @JoinColumn(name = "file_id", insertable = false, updatable = false)
    var file: File? = null

}
