package bo.edu.umsa.backend.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import java.io.IOException


@Configuration
class FirebaseConfig {

    @Bean
    fun firebaseApp(): FirebaseApp {
        try {
            val credentials = GoogleCredentials.fromStream(ClassPathResource("firebase/serviceAccountKey.json").inputStream)
            val options = FirebaseOptions.builder().setCredentials(credentials).build()

            return FirebaseApp.initializeApp(options)
        } catch (e: IOException) {
            throw RuntimeException("Error initializing Firebase", e)
        }
    }
}
