package bo.edu.umsa.backend.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class EmailService @Autowired constructor(private val javaMailSender: JavaMailSender) {

    @Value("\${spring.mail.username}")
    private val from: String? = null

    companion object {
        private val logger = org.slf4j.LoggerFactory.getLogger(EmailService::class.java.name)
    }

    @Async
    fun sendEmail(to: String?, subject: String?, content: String?) {
        // Validate parameters before sending the email
        if (to == null || subject == null || content == null) {
            logger.error("Error: Email not sent. Missing parameters")
            return
        }
        val message = SimpleMailMessage()
        message.from = from
        message.setTo(to)
        message.subject = subject
        message.text = content
        javaMailSender.send(message)
    }
}