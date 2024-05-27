package bo.edu.umsa.backend.dto

data class ProjectReportFiltersDto (
    val statuses: List<ProjectReportStatusDto>,
    val projectOwners: List<ProjectReportOwnerDto>,
    val projectModerators: List<ProjectReportModeratorDto>,
    val projectMembers: List<ProjectReportMemberDto>,
)

data class ProjectReportStatusDto (
    val statusId: Int,
    val statusName: String,
)

data class ProjectReportOwnerDto (
    val userId: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
)

data class ProjectReportModeratorDto (
    val userId: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
)

data class ProjectReportMemberDto (
    val userId: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
)