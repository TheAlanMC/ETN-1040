package bo.edu.umsa.backend.dto

data class NewTaskFeedbackDto(
    val taskRating: Int,
    val taskRatingComment: String,
)