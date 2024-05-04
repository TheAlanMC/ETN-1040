package bo.edu.umsa.backend.dto

data class NewUserDto (
    val groupId: Int,
    val email: String,
    val firstName: String,
    val lastName: String,
    val phone: String,
    val description: String,
)
