package bo.edu.umsa.backend.dto

data class UserDto (
    val userId: Int,
    val email: String,
    val firstName: String,
    val lastName: String,
    val phone: String,
    val description: String,
    val txUser: String,
)
