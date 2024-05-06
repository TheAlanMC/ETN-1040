package bo.edu.umsa.backend.dto

data class FileDto (
    val filename: String,
    val contentType: String,
    val fileData: ByteArray,
    val thumbnail: ByteArray?,
)