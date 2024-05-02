package bo.edu.umsa.backend.util

import bo.edu.umsa.backend.config.JwtConfig
import bo.edu.umsa.backend.dto.AuthResDto
import bo.edu.umsa.backend.entity.User
import bo.edu.umsa.backend.exception.EtnException
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.exceptions.SignatureVerificationException
import com.auth0.jwt.exceptions.TokenExpiredException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.time.Instant
import java.util.*

@Component
class AuthUtil (@Autowired jwtConfig: JwtConfig) {

    init {
        jwtIssuer = jwtConfig.issuer
        jwtSecret = jwtConfig.secret
        jwtExpirationTime = jwtConfig.expirationTime
    }

    companion object {
        lateinit var jwtIssuer: String
        lateinit var jwtSecret: String
        var jwtExpirationTime: Int = 0

        private fun getAuthToken(): String? {
            val requestAttributes = RequestContextHolder.getRequestAttributes() as ServletRequestAttributes
            val request = requestAttributes.request
            val authHeader = request.getHeader("Authorization")
            val token = authHeader?.split(" ")?.get(1)
            return token
        }

        private fun verifyIsAuthToken(jwtToken: String?): Boolean {
            if (jwtToken == null ) throw EtnException(HttpStatus.UNAUTHORIZED, "Error: Missing authentication token","Token de autenticación faltante")
            try {
                JWT.require(Algorithm.HMAC256(jwtSecret))
                    .build()
                    .verify(jwtToken)
                return true
            } catch (e: JWTVerificationException) {
                throw EtnException(HttpStatus.UNAUTHORIZED, "Error: Invalid authentication token","Token de autenticación inválido")
            } catch (e: SignatureVerificationException){
                throw EtnException(HttpStatus.UNAUTHORIZED, "Error: Invalid authentication token","Token de autenticación inválido")
            } catch (e: TokenExpiredException){
                throw EtnException(HttpStatus.UNAUTHORIZED, "Error: Expired authentication token","Token de autenticación expirado")
            }
        }

       fun verifyIsRefreshToken(jwtToken: String?): Boolean {
            if (jwtToken == null ) throw EtnException(HttpStatus.UNAUTHORIZED, "Error: Missing refresh token","Token de refresco faltante")
            try {
                val isRefresh = JWT.require(Algorithm.HMAC256(jwtSecret))
                    .build()
                    .verify(jwtToken)
                    .getClaim("refresh")
                    .asBoolean()
                if (!isRefresh) throw EtnException(HttpStatus.UNAUTHORIZED, "Error: Invalid refresh token","Token de refresco inválido")
                return true
            } catch (e: JWTVerificationException) {
                throw EtnException(HttpStatus.UNAUTHORIZED, "Error: Invalid refresh token","Token de refresco inválido")
            } catch (e: SignatureVerificationException){
                throw EtnException(HttpStatus.UNAUTHORIZED, "Error: Invalid refresh token","Token de refresco inválido")
            } catch (e: TokenExpiredException){
                throw EtnException(HttpStatus.UNAUTHORIZED, "Error: Expired refresh token","Token de refresco expirado")
            }
        }

        fun verifyAuthTokenHasRole(role: String) {
            val jwtToken = getAuthToken()
            verifyIsAuthToken(jwtToken)
            val roles = JWT.require(Algorithm.HMAC256(jwtSecret))
                .build()
                .verify(jwtToken)
                .getClaim("roles")
                .asList(String::class.java)
            if (!roles.contains(role)) {
                throw EtnException(HttpStatus.FORBIDDEN, "Error: User is not authorized to perform this action","El usuario no está autorizado para realizar esta acción")
            }
        }

        fun generateAuthAndRefreshToken(userEntity: User, roles: Array<String>, groups: Array<String>): AuthResDto {
            val algorithm = Algorithm.HMAC256(jwtSecret)
            val jwtToken = JWT.create()
                .withIssuer(jwtIssuer)
                .withSubject(userEntity.username)
                .withArrayClaim("groups", groups)
                .withArrayClaim("roles", roles)
                .withClaim("refresh", false)
                .withClaim("userId", userEntity.userId)
                .withClaim("email", userEntity.email)
                .withClaim("name", userEntity.firstName + " " + userEntity.lastName)
                .withClaim("givenName", userEntity.firstName)
                .withClaim("familyName", userEntity.lastName)
                .withIssuedAt(Date.from(Instant.now()))
                .withExpiresAt(Date.from(Instant.now().plusSeconds(jwtExpirationTime.toLong())))
                .sign(algorithm)
            val refreshToken = JWT.create()
                .withIssuer(jwtIssuer)
                .withSubject(userEntity.username)
                .withClaim("refresh", true)
                .withIssuedAt(Date.from(Instant.now()))
                .withExpiresAt(Date.from(Instant.now().plusSeconds((jwtExpirationTime * 2).toLong())))
                .sign(algorithm)
            return AuthResDto(jwtToken, refreshToken)
        }

        // Methods to get fields from the token
        fun getUsernameFromAuthToken(token: String? = null): String? {
            val jwtToken = token ?: getAuthToken()
            return try {
                JWT.require(Algorithm.HMAC256(jwtSecret))
                    .build()
                    .verify(jwtToken)
                    .getClaim("sub")
                    .asString()
            } catch (e: Exception) {
                null
            }
        }
    }
}
