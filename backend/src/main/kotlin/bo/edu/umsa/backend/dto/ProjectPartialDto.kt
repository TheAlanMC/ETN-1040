package bo.edu.umsa.backend.dto

data class ProjectPartialDto(
    val projectId: Int,
    val projectName: String,
    val projectOwnerIds: List<Int>,
)
