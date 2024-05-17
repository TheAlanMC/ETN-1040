package bo.edu.umsa.backend.exception

import org.springframework.http.HttpStatus

class EtnException(val httpStatus: HttpStatus, val logMessage: String, val errorMessage: String) : Exception()