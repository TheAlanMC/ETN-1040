package bo.edu.umsa.backend.dto

import java.sql.Timestamp

data class ProjectDto(
    val projectId: Int,
    val projectName: String,
    val projectDescription: String,
    val dateFrom: Timestamp,
    val dateTo: Timestamp,
    val projectOwners: List<UserPartialDto>,
    val projectModerators: List<UserPartialDto>,
    val projectMembers: List<UserPartialDto>,
)
