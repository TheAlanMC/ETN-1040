package bo.edu.umsa.backend.dto

data class AuthReqDto(
    val email: String,
    val password: String,
    val firebaseToken: String,
    val isMobile: Boolean
)