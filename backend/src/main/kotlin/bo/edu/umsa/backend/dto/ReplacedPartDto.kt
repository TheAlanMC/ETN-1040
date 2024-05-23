package bo.edu.umsa.backend.dto

import java.sql.Timestamp

data class ReplacedPartDto(
    val replacedPartId: Int,
    val replacedPartDescription: String,
    val txDate: Timestamp,
    val replacedPartFiles: List<FilePartialDto>,
)
