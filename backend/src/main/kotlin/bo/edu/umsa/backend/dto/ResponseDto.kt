package bo.edu.umsa.backend.dto

data class ResponseDto<T>(
    val successful: Boolean,
    val message: String,
    val data: T?,
)