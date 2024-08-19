package bo.edu.umsa.backend.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter

@Configuration
class CorsConfig {

    @Bean
    fun corsFilter(): CorsFilter {
        val corsConfiguration = CorsConfiguration()
        corsConfiguration.allowCredentials = false
        corsConfiguration.allowedOrigins = listOf(
            // Development Server
            "http://localhost:4200", "http://localhost",
            // VPN Server
            "http://192.168.192.168:4200", "http://192.168.192.168",
            // VPN Secure Server
            "https://192.168.192.168:4200", "https://192.168.192.168",
            // Firebase Hosting
            "https://laboratorio-multimedia.web.app", "https://laboratorio-multimedia.firebaseapp.com",
            // Reverse Proxy
            "https://laboratoriomultimedia.xyz", "https://www.laboratoriomultimedia.xyz",
            // Mobile App
            "capacitor://localhost")
        corsConfiguration.allowedHeaders = listOf(
            "Origin",
            "Access-Control-Allow-Origin",
            "Content-Type", "Accept", "Authorization",
            "Origin, Accept", "X-Requested-With",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers",
        )
        corsConfiguration.exposedHeaders = listOf("Origin", "Content-Type", "Accept", "Authorization", "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials", "content-disposition")
        corsConfiguration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE")
        val urlBasedCorsConfigurationSource = UrlBasedCorsConfigurationSource()
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration)
        return CorsFilter(urlBasedCorsConfigurationSource)
    }
}