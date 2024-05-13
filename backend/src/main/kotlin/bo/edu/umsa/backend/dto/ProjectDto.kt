package bo.edu.umsa.backend.dto

import java.sql.Timestamp

data class ProjectDto(
    val projectId: Int,
    val projectName: String,
    val projectDescription: String,
    val dateFrom: Timestamp,
    val dateTo: Timestamp,
    val projectOwnerIds: List<Int>,
    val projectModeratorIds: List<Int>,
    val projectMemberIds: List<Int>,
)
