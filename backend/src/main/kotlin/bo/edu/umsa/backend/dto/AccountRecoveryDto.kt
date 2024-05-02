package bo.edu.umsa.backend.dto

data class AccountRecoveryDto (
    var email: String = "",
    var code: String = "",
    var password: String = "",
    var confirmPassword: String = ""
)