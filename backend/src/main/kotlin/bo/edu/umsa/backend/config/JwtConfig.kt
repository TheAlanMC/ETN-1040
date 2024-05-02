package bo.edu.umsa.backend.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "security.jwt")
class JwtConfig {
    lateinit var issuer: String
    lateinit var secret: String
    var expirationTime: Int = 3600  // Default value set to 3600 seconds
}