package bo.edu.umsa.backend.dto

data class NewProjectDto (
    val projectName: String,
    val projectDescription: String,
    val dateFrom: String,
    val dateTo: String,
    val projectModeratorIds: List<Int>,
    val projectMemberIds: List<Int>,
)