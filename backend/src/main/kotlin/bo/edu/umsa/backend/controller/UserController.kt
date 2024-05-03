package bo.edu.umsa.backend.controller

import bo.edu.umsa.backend.dto.FileDto
import bo.edu.umsa.backend.dto.ResponseDto
import bo.edu.umsa.backend.dto.UserDto
import bo.edu.umsa.backend.service.UserService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.*
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/v1/users")
class UserController @Autowired constructor(
    private val userService: UserService
) {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(UserController::class.java)
    }

    @GetMapping("/profile-picture")
    fun getProfilePicture(): ResponseEntity<ByteArray> {
        logger.info("Starting the API call to get the profile picture")
        logger.info("GET /api/v1/auth/users/profile-picture")
        val profilePicture: FileDto = userService.getProfilePicture()
        val headers = HttpHeaders()
        headers.contentType = MediaType.parseMediaType(profilePicture.contentType)
        headers.contentDisposition = ContentDisposition.parse("inline; filename=${profilePicture.filename}")
        logger.info("Success: Profile picture retrieved")
        return ResponseEntity(profilePicture.fileData, headers, HttpStatus.OK)
    }

    @PostMapping("/profile-picture")
    fun uploadProfilePicture(
        @RequestParam("file") file: MultipartFile
    ): ResponseEntity<ResponseDto<Nothing>> {
        logger.info("Starting the API call to upload the profile picture")
        logger.info("POST /api/v1/auth/users/profile-picture")
        userService.uploadProfilePicture(file)
        logger.info("Success: Profile picture uploaded")
        return ResponseEntity(ResponseDto(true,"La foto de perfil se ha subido", null), HttpStatus.OK)
    }

    @GetMapping("/profile")
    fun getProfile(): ResponseEntity<ResponseDto<UserDto>> {
        logger.info("Starting the API call to get the profile")
        logger.info("GET /api/v1/auth/users/profile")
        val userDto: UserDto = userService.getProfile()
        logger.info("Success: Profile retrieved")
        return ResponseEntity(ResponseDto(true,"Perfil recuperado", userDto), HttpStatus.OK)
    }

    @PutMapping("/profile")
    fun updateProfile(
        @RequestBody userDto: UserDto,
    ): ResponseEntity<ResponseDto<Nothing>> {
        logger.info("Starting the API call to update the profile")
        logger.info("PUT /api/v1/auth/users/profile")
        userService.updateProfile(userDto)
        logger.info("Success: Profile updated")
        return ResponseEntity(ResponseDto(true,"El perfil se ha actualizado", null), HttpStatus.OK)
    }
}
