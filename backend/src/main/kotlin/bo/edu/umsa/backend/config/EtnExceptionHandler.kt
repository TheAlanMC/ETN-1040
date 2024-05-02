package bo.edu.umsa.backend.config

import bo.edu.umsa.backend.dto.ResponseDto
import bo.edu.umsa.backend.exception.EtnException
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class EtnExceptionHandler {
    companion object {
        private val logger = LoggerFactory.getLogger(EtnExceptionHandler::class.java.name)
    }

    @ExceptionHandler(EtnException::class)
    fun handleUasException(ex: EtnException): ResponseEntity<ResponseDto<Nothing>> {
        logger.error(ex.logMessage)
        return ResponseEntity(ResponseDto(false, ex.errorMessage,null), ex.httpStatus)
    }
}