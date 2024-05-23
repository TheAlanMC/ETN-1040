package bo.edu.umsa.backend.dto

data class FileDto(
    val fileId: Int,
    val fileName: String,
    val contentType: String,
    val fileSize: Int,
    val fileData: ByteArray?,
    val thumbnail: ByteArray?,
)