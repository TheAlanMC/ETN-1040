package bo.edu.umsa.backend.service

import bo.edu.umsa.backend.dto.NotificationDto
import bo.edu.umsa.backend.entity.User
import bo.edu.umsa.backend.exception.EtnException
import bo.edu.umsa.backend.mapper.NotificationMapper
import bo.edu.umsa.backend.repository.NotificationRepository
import bo.edu.umsa.backend.repository.UserRepository
import bo.edu.umsa.backend.util.AuthUtil
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class NotificationService @Autowired constructor(
    private val userRepository: UserRepository,
    private val notificationRepository: NotificationRepository
) {
    companion object {
        private val logger = LoggerFactory.getLogger(NotificationService::class.java)
    }

    fun getNotifications(): List<NotificationDto> {
        val userId = AuthUtil.getUserIdFromAuthToken() ?: throw EtnException(HttpStatus.UNAUTHORIZED, "Error: Unauthorized", "No autorizado")
        logger.info("Getting the notifications of $userId")
        // Get the user
        val userEntity: User = userRepository.findByUserIdAndStatusIsTrue(userId)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: User not found", "Usuario no encontrado")
        val notificationEntities = notificationRepository.findAllByUserIdAndStatusIsTrueOrderByTxDateDesc(userEntity.userId.toLong())
        return notificationEntities.map { notificationEntity -> NotificationMapper.entityToDto(notificationEntity) }
    }

    fun markNotificationAsRead(notificationId: Long) {
        val userId = AuthUtil.getUserIdFromAuthToken() ?: throw EtnException(HttpStatus.UNAUTHORIZED, "Error: Unauthorized", "No autorizado")
        logger.info("Marking the notification with id $notificationId as read")
        // Get the user
        val userEntity: User = userRepository.findByUserIdAndStatusIsTrue(userId)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: User not found", "Usuario no encontrado")
        // Get the notification
        val notificationEntity = notificationRepository.findByNotificationIdAndStatusIsTrue(notificationId)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: Notification not found", "Notificación no encontrada")
        // Mark the notification as read
        notificationEntity.status = false
        notificationRepository.save(notificationEntity)
    }
}
