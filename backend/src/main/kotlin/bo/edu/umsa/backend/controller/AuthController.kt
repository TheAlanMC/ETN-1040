package bo.edu.umsa.backend.controller

import bo.edu.umsa.backend.dto.AccountRecoveryDto
import bo.edu.umsa.backend.dto.AuthReqDto
import bo.edu.umsa.backend.dto.AuthResDto
import bo.edu.umsa.backend.dto.ResponseDto
import bo.edu.umsa.backend.service.AuthService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthController @Autowired constructor(
    private val authService: AuthService
){
    companion object {
        val logger: Logger = LoggerFactory.getLogger(AuthController::class.java)
    }

    @PostMapping("/login")
    fun authenticate(
        @RequestBody authReqDto: AuthReqDto
    ): ResponseEntity<ResponseDto<AuthResDto>> {
        logger.info("Starting the API call to authenticate")
        logger.info("POST /api/v1/auth")
        val authResDto: AuthResDto = authService.authenticate(authReqDto)
        logger.info("Success: Credentials are correct")
        return ResponseEntity(ResponseDto(true,"Las credenciales son correctas", authResDto), HttpStatus.OK)
    }

    @PostMapping("/refresh-token")
    fun refreshToken(
        @RequestBody refreshToken: Map<String, String>
    ): ResponseEntity<ResponseDto<AuthResDto>> {
        logger.info("Starting the API call to refresh the token")
        logger.info("POST /api/v1/auth/refresh")
        val authResDto: AuthResDto = authService.refreshToken(refreshToken["refreshToken"]!!)
        logger.info("Success: Token refreshed")
        return ResponseEntity(ResponseDto(true,"El token se ha refrescado", authResDto), HttpStatus.OK)
    }

    @PostMapping("/forgot-password")
    fun forgotPassword(
        @RequestBody accountRecoveryDto: AccountRecoveryDto
    ): ResponseEntity<ResponseDto<Nothing>> {
        logger.info("Starting the API call to send an email to reset the password")
        logger.info("POST /api/v1/auth/forgot-password")
        authService.forgotPassword(accountRecoveryDto.email)
        logger.info("Success: Email sent")
        return ResponseEntity(ResponseDto(true,"El correo ha sido enviado", null), HttpStatus.OK)
    }

    @PostMapping("/verification")
    fun verification(
        @RequestBody accountRecoveryDto: AccountRecoveryDto
    ): ResponseEntity<ResponseDto<Nothing>> {
        logger.info("Starting the API call to verify the hash code")
        logger.info("POST /api/v1/auth/verification")
        authService.verification(accountRecoveryDto.email, accountRecoveryDto.code)
        logger.info("Success: Hash code verified")
        return ResponseEntity(ResponseDto(true,"El código ha sido verificado", null), HttpStatus.OK)
    }

    @PostMapping("/reset-password")
    fun resetPassword(
        @RequestBody accountRecoveryDto: AccountRecoveryDto
    ): ResponseEntity<ResponseDto<Nothing>> {
        logger.info("Starting the API call to reset the password")
        logger.info("POST /api/v1/auth/reset-password")
        authService.resetPassword(accountRecoveryDto.email, accountRecoveryDto.code, accountRecoveryDto.password, accountRecoveryDto.confirmPassword)
        logger.info("Success: Password reset")
        return ResponseEntity(ResponseDto(true,"La contraseña ha sido cambiada", null), HttpStatus.OK)
    }

}