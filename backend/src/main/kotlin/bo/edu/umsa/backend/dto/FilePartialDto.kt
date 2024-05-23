package bo.edu.umsa.backend.dto

data class FilePartialDto(
    val fileId: Int,
    val fileName: String,
    val contentType: String,
    val fileSize: Int,
)