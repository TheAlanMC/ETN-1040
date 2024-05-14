package bo.edu.umsa.backend.dto

data class NewToolDto(
    val filePhotoId: Int,
    val toolCode: String,
    val toolName: String,
    val toolDescription: String,
)