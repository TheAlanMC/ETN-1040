package bo.edu.umsa.backend.dto

import java.sql.Timestamp

data class TaskHistoryDto (
    val user: UserPartialDto,
    val fieldName: String,
    val previousValue: String,
    val newValue: String,
    val txDate: Timestamp,
)