package bo.edu.umsa.backend.dto

import java.sql.Timestamp

data class NewProjectDto (
    val projectName: String,
    val dateFrom: Timestamp,
    val dateTo: Timestamp,
    val projectDescription: String,
    val projectModeratorIds: List<Int>,
    val projectMemberIds: List<Int>,
)