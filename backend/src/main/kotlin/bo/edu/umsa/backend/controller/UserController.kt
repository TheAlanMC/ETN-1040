package bo.edu.umsa.backend.controller

import bo.edu.umsa.backend.dto.*
import bo.edu.umsa.backend.service.UserService
import bo.edu.umsa.backend.util.AuthUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.http.*
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/v1/users")
class UserController @Autowired constructor(private val userService: UserService) {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(UserController::class.java)
    }

    @GetMapping("/all")
    fun getAllUsers(): ResponseEntity<ResponseDto<List<UserPartialDto>>> {
        logger.info("Starting the API call to get all users")
        logger.info("GET /api/v1/users/all")
        AuthUtil.verifyAuthTokenHasRole("GESTIONAR ROLES Y PERMISOS")
        val users: List<UserPartialDto> = userService.getAllUsers()
        logger.info("Success: Users retrieved")
        return ResponseEntity(ResponseDto(true, "Usuarios recuperados", users), HttpStatus.OK)
    }

    @GetMapping
    fun getUsers(@RequestParam(defaultValue = "userId") sortBy: String, @RequestParam(defaultValue = "asc") sortType: String, @RequestParam(defaultValue = "0") page: Int, @RequestParam(defaultValue = "10") size: Int, @RequestParam(required = false) keyword: String?): ResponseEntity<ResponseDto<Page<UserDto>>> {
        logger.info("Starting the API call to get the users")
        logger.info("GET /api/v1/users")
        AuthUtil.verifyAuthTokenHasRole("VER USUARIOS")
        val users: Page<UserDto> = userService.getUsers(sortBy, sortType, page, size, keyword)
        logger.info("Success: Users retrieved")
        return ResponseEntity(ResponseDto(true, "Usuarios recuperados", users), HttpStatus.OK)
    }

    @GetMapping("/{userId}")
    fun getUserById(@PathVariable userId: Long): ResponseEntity<ResponseDto<UserDto>> {
        logger.info("Starting the API call to get the user")
        logger.info("GET /api/v1/users/{userId}")
        AuthUtil.verifyAuthTokenHasRole("VER USUARIOS")
        val user: UserDto = userService.getUserById(userId)
        logger.info("Success: User retrieved")
        return ResponseEntity(ResponseDto(true, "Usuario recuperado", user), HttpStatus.OK)
    }


    @PostMapping
    fun createUser(@RequestBody newUserDto: NewUserDto): ResponseEntity<ResponseDto<Nothing>> {
        logger.info("Starting the API call to create the user")
        logger.info("POST /api/v1/users")
        AuthUtil.verifyAuthTokenHasRole("CREAR USUARIOS")
        userService.createUser(newUserDto)
        logger.info("Success: User created")
        return ResponseEntity(ResponseDto(true, "El usuario se ha creado", null), HttpStatus.CREATED)
    }

    @PutMapping("/{userId}")
    fun updateUser(@PathVariable userId: Long, @RequestBody profileDto: ProfileDto): ResponseEntity<ResponseDto<Nothing>> {
        logger.info("Starting the API call to update the user")
        logger.info("PUT /api/v1/users/{userId}")
        AuthUtil.verifyAuthTokenHasRole("EDITAR USUARIOS")
        userService.updateUser(userId, profileDto)
        logger.info("Success: User updated")
        return ResponseEntity(ResponseDto(true, "El usuario se ha actualizado", null), HttpStatus.OK)
    }

    @DeleteMapping("/{userId}")
    fun deleteUser(@PathVariable userId: Long): ResponseEntity<ResponseDto<Nothing>> {
        logger.info("Starting the API call to delete the user")
        logger.info("DELETE /api/v1/users/{userId}")
        AuthUtil.verifyAuthTokenHasRole("EDITAR USUARIOS")
        userService.deleteUser(userId)
        logger.info("Success: User deleted")
        return ResponseEntity(ResponseDto(true, "El usuario se ha eliminado", null), HttpStatus.OK)
    }

    @GetMapping("/{userId}/profile-picture")
    fun getProfilePicture(@PathVariable userId: Long): ResponseEntity<ByteArray> {
        logger.info("Starting the API call to get the profile picture")
        logger.info("GET /api/v1/users/{userId}/profile-picture")
        AuthUtil.verifyAuthTokenHasRole("VER USUARIOS")
        val profilePicture: FileDto = userService.getProfilePicture(userId)
        val headers = HttpHeaders()
        headers.contentType = MediaType.parseMediaType(profilePicture.contentType)
        headers.contentDisposition = ContentDisposition.parse("inline; filename=${profilePicture.filename}")
        UserProfileController.logger.info("Success: Profile picture retrieved")
        return ResponseEntity(profilePicture.fileData, headers, HttpStatus.OK)
    }

    @GetMapping("/{userId}/profile-picture/thumbnail")
    fun getProfilePictureThumbnail(@PathVariable userId: Long): ResponseEntity<ByteArray> {
        logger.info("Starting the API call to get the profile picture thumbnail")
        logger.info("GET /api/v1/users/{userId}/profile-picture/thumbnail")
        val profilePicture: FileDto = userService.getProfilePictureThumbnail(userId)
        val headers = HttpHeaders()
        headers.contentType = MediaType.parseMediaType(profilePicture.contentType)
        headers.contentDisposition = ContentDisposition.parse("inline; filename=${profilePicture.filename}")
        logger.info("Success: Profile picture thumbnail retrieved")
        return ResponseEntity(profilePicture.thumbnail, headers, HttpStatus.OK)
    }

    @PutMapping("/{userId}/profile-picture")
    fun updateProfilePicture(@PathVariable userId: Long, @RequestParam("file") file: MultipartFile): ResponseEntity<ResponseDto<Nothing>> {
        logger.info("Starting the API call to update the profile picture")
        logger.info("PUT /api/v1/users/{userId}/profile-picture")
        AuthUtil.verifyAuthTokenHasRole("EDITAR USUARIOS")
        userService.uploadProfilePicture(userId, file)
        logger.info("Success: Profile picture updated")
        return ResponseEntity(ResponseDto(true, "Foto de perfil actualizada", null), HttpStatus.OK)
    }


    @GetMapping("/{userId}/groups")
    fun getGroupsByUserId(@PathVariable userId: Long): ResponseEntity<ResponseDto<List<GroupDto>>> {
        logger.info("Starting the API call to get the groups by user id")
        logger.info("GET /api/v1/users/{userId}/groups")
        AuthUtil.verifyAuthTokenHasRole("GESTIONAR ROLES Y PERMISOS")
        val groups: List<GroupDto> = userService.getGroupsByUserId(userId)
        logger.info("Success: Groups retrieved")
        return ResponseEntity(ResponseDto(true, "Grupos recuperados", groups), HttpStatus.OK)
    }

    @PostMapping("/{userId}/groups")
    fun addGroupsToUser(@PathVariable userId: Long, @RequestBody groupIds: Map<String, List<Long>>): ResponseEntity<ResponseDto<Nothing>> {
        logger.info("Starting the API call to add groups to user")
        logger.info("POST /api/v1/users/{userId}/groups")
        AuthUtil.verifyAuthTokenHasRole("GESTIONAR ROLES Y PERMISOS")
        userService.addGroupsToUser(userId, groupIds["groupIds"]!!)
        logger.info("Success: Groups added to user")
        return ResponseEntity(ResponseDto(true, "Grupos agregados al usuario", null), HttpStatus.CREATED)
    }

}
