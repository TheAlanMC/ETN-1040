package bo.edu.umsa.backend.dto

import java.sql.Timestamp

data class ReportDto(
    val reportId: Int,
    val user: UserPartialDto,
    val file: FilePartialDto,
    val reportName: String,
    val reportType: String,
    val reportStartDate: Timestamp,
    val reportEndDate: Timestamp,
    val txDate: Timestamp,
)
