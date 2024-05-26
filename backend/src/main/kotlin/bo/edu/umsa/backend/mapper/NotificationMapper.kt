package bo.edu.umsa.backend.mapper

import bo.edu.umsa.backend.dto.NotificationDto
import bo.edu.umsa.backend.entity.Notification


class NotificationMapper {
    companion object {
        fun entityToDto(notification: Notification): NotificationDto {
            return NotificationDto(
                notificationId = notification.notificationId,
                messageTitle = notification.messageTitle,
                messageBody = notification.messageBody, txDate = notification.txDate
            )
        }
    }
}