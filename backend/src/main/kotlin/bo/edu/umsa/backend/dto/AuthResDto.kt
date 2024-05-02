package bo.edu.umsa.backend.dto

data class AuthResDto (
    val token: String,
    val refreshToken: String
)