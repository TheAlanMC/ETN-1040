package bo.edu.umsa.backend.dto

import java.sql.Timestamp

data class TaskHistoryDto (
    val createdDate: Timestamp,
    val createdBy: UserPartialDto,
    val fieldName: String,
    val previousValue: String,
    val newValue: String,
)