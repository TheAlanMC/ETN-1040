package bo.edu.umsa.backend.dto

data class NewUserDto(
    val roleId: Int,
    val email: String,
    val firstName: String,
    val lastName: String,
    val phone: String,
    val description: String,
)
