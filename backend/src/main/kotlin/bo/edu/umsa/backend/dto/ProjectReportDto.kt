package bo.edu.umsa.backend.dto

import java.sql.Timestamp

data class ProjectReportDto(
    val projectId: Int,
    val projectName: String,
    val projectStatusName: String,
    val projectFinishedTasks: Int,
    val projectTotalTasks: Int,
    val projectDateFrom: Timestamp,
    val projectDateTo: Timestamp,
    val projectEndDate: Timestamp?,
    val projectOwners: List<String>,
    val projectModerators: List<String>,
    val projectMembers: List<String>,
    val taskReports: List<TaskReportDto>,
)

