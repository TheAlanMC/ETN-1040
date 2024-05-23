package bo.edu.umsa.backend.service

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class FirebaseMessagingService {

    companion object {
        private val logger = LoggerFactory.getLogger(FirebaseMessagingService::class.java.name)
    }

    @Async
    fun sendNotification(
        token: String,
        title: String,
        body: String
    ) {
        if (token.trim().isEmpty() || title.trim().isEmpty() || body.trim().isEmpty()) {
            logger.error("Error: Email not sent. Missing parameters")
            return
        }
        try {
            val message = Message.builder().setToken(token).setNotification(Notification.builder().setTitle(title).setBody(body).setImage("https://cvinge.umsa.bo/pluginfile.php/29/coursecat/description/logo.jpg").build()).build()
            // Send a message to the device corresponding to the provided token
            val response = FirebaseMessaging.getInstance().send(message)
            // Log the response
            logger.info("Successfully sent message: $response")
        } catch (e: Exception) {
            logger.error("Error sending FCM message")
        }
    }
}
