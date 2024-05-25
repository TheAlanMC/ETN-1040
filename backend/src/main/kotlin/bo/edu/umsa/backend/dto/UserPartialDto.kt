package bo.edu.umsa.backend.dto

import java.sql.Timestamp


data class UserPartialDto(
    val userId: Int,
    val email: String,
    val firstName: String,
    val lastName: String,
    val phone: String,
    val txDate: Timestamp,
    val txHost: String,
)
