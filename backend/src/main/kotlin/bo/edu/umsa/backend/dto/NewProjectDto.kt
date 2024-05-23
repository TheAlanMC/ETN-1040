package bo.edu.umsa.backend.dto

data class NewProjectDto(
    val projectName: String,
    val projectDescription: String,
    val projectObjective: String,
    val projectDateFrom: String,
    val projectDateTo: String,
    val projectModeratorIds: List<Int>,
    val projectMemberIds: List<Int>,
)