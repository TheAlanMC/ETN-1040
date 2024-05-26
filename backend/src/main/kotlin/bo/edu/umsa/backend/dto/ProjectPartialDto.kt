package bo.edu.umsa.backend.dto

import java.sql.Timestamp

data class ProjectPartialDto(
    val projectId: Int,
    val projectName: String,
    val projectDateFrom: Timestamp,
    val projectDateTo: Timestamp,
    val projectEndDate: Timestamp?,
    val projectOwners: List<UserPartialDto>,
    val projectModerators: List<UserPartialDto>,
    val projectMembers: List<UserPartialDto>,
)