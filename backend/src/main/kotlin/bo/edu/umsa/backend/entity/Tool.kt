package bo.edu.umsa.backend.entity

import bo.edu.umsa.backend.util.AuthUtil
import bo.edu.umsa.backend.util.HttpUtil
import jakarta.persistence.*
import java.sql.Timestamp

@Entity
@Table(name = "tool", schema = "public")
class Tool {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tool_id")
    var toolId: Int = 0

    @Column(name = "file_photo_id")
    var filePhotoId: Int = 0

    @Column(name = "tool_code")
    var toolCode: String = ""

    @Column(name = "tool_name")
    var toolName: String = ""

    @Column(name = "tool_description")
    var toolDescription: String = ""

    @Column(name = "available")
    var available: Boolean = true

    @Column(name = "status")
    var status: Boolean = true

    @Column(name = "tx_date")
    var txDate: Timestamp = Timestamp(System.currentTimeMillis())

    @Column(name = "tx_user")
    var txUser: String = AuthUtil.getEmailFromAuthToken() ?: "admin"

    @Column(name = "tx_host")
    var txHost: String = HttpUtil.getRequestHost() ?: "localhost"

    @ManyToOne
    @JoinColumn(name = "file_photo_id", referencedColumnName = "file_id", insertable = false, updatable = false)
    var file: File? = null

    @OneToMany(mappedBy = "tool")
    var loanedTools: List<LoanedTool>? = null

}
