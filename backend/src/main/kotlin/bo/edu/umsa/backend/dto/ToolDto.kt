package bo.edu.umsa.backend.dto

data class ToolDto(
    val toolId: Int,
    val filePhotoId: Int,
    val toolCode: String,
    val toolName: String,
    val toolDescription: String,
    val available: Boolean,
)