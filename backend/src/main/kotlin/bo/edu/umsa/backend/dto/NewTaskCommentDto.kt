package bo.edu.umsa.backend.dto

data class NewTaskCommentDto(val taskId: Int, val comment: String, val taskCommentFileIds: List<Int>)