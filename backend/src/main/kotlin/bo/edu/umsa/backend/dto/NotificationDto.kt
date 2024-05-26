package bo.edu.umsa.backend.dto

import java.sql.Timestamp

data class NotificationDto(
    val notificationId: Int,
    val messageTitle: String,
    val messageBody: String,
    val txDate: Timestamp
)
