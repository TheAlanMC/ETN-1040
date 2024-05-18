package bo.edu.umsa.backend.entity

import bo.edu.umsa.backend.util.AuthUtil
import bo.edu.umsa.backend.util.HttpUtil
import jakarta.persistence.*
import java.sql.Timestamp

@Entity
@Table(name = "file", schema = "public")
class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    var fileId: Int = 0

    @Column(name = "content_type")
    var contentType: String = ""

    @Column(name = "filename")
    var filename: String = ""

    @Column(name = "file_size")
    var fileSize: Int = 0

    @Column(name = "file_data")
    var fileData: ByteArray = byteArrayOf()

    @Column(name = "is_picture")
    var isPicture: Boolean = false

    @Column(name = "thumbnail")
    var thumbnail: ByteArray = byteArrayOf()

    @Column(name = "status")
    var status: Boolean = true

    @Column(name = "tx_date")
    var txDate: Timestamp = Timestamp(System.currentTimeMillis())

    @Column(name = "tx_user")
    var txUser: String = AuthUtil.getEmailFromAuthToken() ?: "admin"

    @Column(name = "tx_host")
    var txHost: String = HttpUtil.getRequestHost() ?: "localhost"

}
