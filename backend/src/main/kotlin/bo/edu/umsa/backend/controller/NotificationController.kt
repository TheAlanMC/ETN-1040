package bo.edu.umsa.backend.controller

import bo.edu.umsa.backend.dto.NotificationDto
import bo.edu.umsa.backend.dto.ProfileDto
import bo.edu.umsa.backend.dto.ResponseDto
import bo.edu.umsa.backend.service.NotificationService
import bo.edu.umsa.backend.util.AuthUtil
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/notifications")
class NotificationController @Autowired constructor(private val notificationService: NotificationService) {

    companion object {
        private val logger = LoggerFactory.getLogger(NotificationController::class.java.name)
    }

    @GetMapping
    fun getNotifications(): ResponseEntity<ResponseDto<List<NotificationDto>>> {
        logger.info("Getting the notifications")
        val notifications: List<NotificationDto> = notificationService.getNotifications()
        logger.info("Success: Notifications retrieved")
        return ResponseEntity(ResponseDto(true, "Notificaciones recuperadas", notifications), HttpStatus.OK)
    }

    @GetMapping("/mark-as-read")
    fun markNotificationAsRead(notificationId: Int): ResponseEntity<ResponseDto<Nothing>> {
        logger.info("Marking the notification as read")
        notificationService.markNotificationAsRead(notificationId)
        logger.info("Success: Notification marked as read")
        return ResponseEntity(ResponseDto(true, "Notificación marcada como leída", null), HttpStatus.OK)
    }
}