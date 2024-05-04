package bo.edu.umsa.backend.controller

import bo.edu.umsa.backend.dto.FileDto
import bo.edu.umsa.backend.dto.PasswordChangeDto
import bo.edu.umsa.backend.dto.ResponseDto
import bo.edu.umsa.backend.dto.ProfileDto
import bo.edu.umsa.backend.service.UserProfileService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.*
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/v1/profile")
class UserProfileController @Autowired constructor(
    private val userProfileService: UserProfileService
) {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(UserProfileController::class.java)
    }

    @GetMapping
    fun getProfile(): ResponseEntity<ResponseDto<ProfileDto>> {
        logger.info("Starting the API call to get the profile")
        logger.info("GET /api/v1/auth/users/profile")
        val profileDto: ProfileDto = userProfileService.getProfile()
        logger.info("Success: Profile retrieved")
        return ResponseEntity(ResponseDto(true,"Perfil recuperado", profileDto), HttpStatus.OK)
    }

    @PutMapping
    fun updateProfile(
        @RequestBody profileDto: ProfileDto,
    ): ResponseEntity<ResponseDto<Nothing>> {
        logger.info("Starting the API call to update the profile")
        logger.info("PUT /api/v1/auth/profile")
        userProfileService.updateProfile(profileDto)
        logger.info("Success: Profile updated")
        return ResponseEntity(ResponseDto(true,"El perfil se ha actualizado", null), HttpStatus.OK)
    }

    @GetMapping("/picture")
    fun getProfilePicture(): ResponseEntity<ByteArray> {
        logger.info("Starting the API call to get the profile picture")
        logger.info("GET /api/v1/auth/profile/picture")
        val profilePicture: FileDto = userProfileService.getProfilePicture()
        val headers = HttpHeaders()
        headers.contentType = MediaType.parseMediaType(profilePicture.contentType)
        headers.contentDisposition = ContentDisposition.parse("inline; filename=${profilePicture.filename}")
        logger.info("Success: Profile picture retrieved")
        return ResponseEntity(profilePicture.fileData, headers, HttpStatus.OK)
    }

    @PutMapping("/picture")
    fun uploadProfilePicture(
        @RequestParam("file") file: MultipartFile
    ): ResponseEntity<ResponseDto<Nothing>> {
        logger.info("Starting the API call to upload the profile picture")
        logger.info("POST /api/v1/auth/profile/picture")
        userProfileService.uploadProfilePicture(file)
        logger.info("Success: Profile picture uploaded")
        return ResponseEntity(ResponseDto(true,"La foto de perfil se ha subido", null), HttpStatus.OK)
    }

    @PutMapping("/password")
    fun updatePassword(
        @RequestBody passwordChangeDto: PasswordChangeDto
    ): ResponseEntity<ResponseDto<Nothing>> {
        logger.info("Starting the API call to update the password")
        logger.info("POST /api/v1/auth/profile/password")
        userProfileService.updatePassword(passwordChangeDto)
        logger.info("Success: Password updated")
        return ResponseEntity(ResponseDto(true,"La contraseña se ha actualizado", null), HttpStatus.OK)
    }
}
