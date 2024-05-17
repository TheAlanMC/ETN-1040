package bo.edu.umsa.backend.dto

data class PasswordChangeDto(var email: String = "", var code: String = "", var password: String = "", var confirmPassword: String = "", var oldPassword: String = "")