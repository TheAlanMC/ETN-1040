package bo.edu.umsa.backend.dto

import java.sql.Timestamp

data class UserDto(
    val userId: Int,
    val email: String,
    val firstName: String,
    val lastName: String,
    val phone: String,
    val description: String,
    val txUser: String,
    val txDate: Timestamp,
    val roles: List<String>,
    val permissions: List<String>,
)
