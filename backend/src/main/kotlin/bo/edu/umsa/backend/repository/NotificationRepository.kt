package bo.edu.umsa.backend.repository

import bo.edu.umsa.backend.entity.Notification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface NotificationRepository : JpaRepository<Notification, Long> {

    fun findAllByUserIdAndStatusIsTrue(userId: Long): List<Notification>

    fun findByNotificationIdAndStatusIsTrue(notificationId: Long): Notification?

}
