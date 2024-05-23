package bo.edu.umsa.backend.dto

data class NewReplacedPartDto(
    val taskId: Int,
    val replacedPartDescription: String,
    val replacedPartFileIds: List<Int>,
)