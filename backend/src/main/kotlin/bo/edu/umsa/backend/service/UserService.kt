package bo.edu.umsa.backend.service

import at.favre.lib.crypto.bcrypt.BCrypt
import bo.edu.umsa.backend.dto.*
import bo.edu.umsa.backend.entity.File
import bo.edu.umsa.backend.entity.User
import bo.edu.umsa.backend.entity.UserRole
import bo.edu.umsa.backend.exception.EtnException
import bo.edu.umsa.backend.mapper.RoleMapper
import bo.edu.umsa.backend.mapper.UserMapper
import bo.edu.umsa.backend.mapper.UserPartialMapper
import bo.edu.umsa.backend.repository.*
import bo.edu.umsa.backend.specification.UserSpecification
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Service
class UserService @Autowired constructor(
    private val assetService: AssetService,
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val fileRepository: FileRepository,
    private val userRoleRepository: UserRoleRepository,
    private val fileService: FileService,
    private val emailService: EmailService,
    private val projectOwnerRepository: ProjectOwnerRepository,
    private val projectModeratorRepository: ProjectModeratorRepository,
    private val projectMemberRepository: ProjectMemberRepository,
    private val taskAssigneeRepository: TaskAssigneeRepository,
) {

    @Value("\${frontend.url}")
    private val url: String? = null

    companion object {
        private val logger = LoggerFactory.getLogger(UserService::class.java)
    }


    fun getAllUsers(): List<UserPartialDto> {
        logger.info("Getting all users")
        val userEntities = userRepository.findAllByStatusIsTrueOrderByUserIdAsc()
        return userEntities.map { UserPartialMapper.entityToDto(it) }
    }

    fun getUsers(
        sortBy: String,
        sortType: String,
        page: Int,
        size: Int,
        keyword: String?
    ): Page<UserPartialDto> {
        logger.info("Getting users")
        // Pagination and sorting
        val pageable: Pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortType), sortBy))
        var specification: Specification<User> = Specification.where(null)
        specification = specification.and(UserSpecification.statusIsTrue())
        // Search by keyword
        if (!keyword.isNullOrEmpty() && keyword.isNotBlank()) {
            specification = specification.and(UserSpecification.userKeyword(keyword))
        }
        val userEntities: Page<User> = userRepository.findAll(specification, pageable)
        return userEntities.map { UserPartialMapper.entityToDto(it) }
    }

    fun getUserById(userId: Long): UserDto {
        logger.info("Getting the user with id $userId")
        // Get the user
        val userEntity: User = userRepository.findByUserIdAndStatusIsTrue(userId)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: User not found", "Usuario no encontrado")
        // Get the user roles
        return UserMapper.entityToDto(userEntity)
    }

    fun createUser(newUserDto: NewUserDto) {
        // Validate that at least these fields are not blank:
        if (newUserDto.email.isBlank() || newUserDto.firstName.isBlank() || newUserDto.lastName.isBlank()) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: At least one required field is blank", "Al menos un campo requerido está en blanco")
        }
        // Validate that the role id is greater than 0
        if (newUserDto.roleId <= 0) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Role was not selected", "Rol no seleccionado")
        }
        // Validate the email format
        if (!newUserDto.email.matches(Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}\$"))) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Invalid email format", "Formato de correo inválido")
        }
        // Phone must be a number
        if (newUserDto.phone.isNotBlank() && !newUserDto.phone.matches(Regex("\\AssistantScheduleDto+"))) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Phone must be a number", "El teléfono debe ser un número")
        }
        // Validate that the role exists
        val roleEntity = roleRepository.findByRoleIdAndStatusIsTrue(newUserDto.roleId.toLong())
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: Role not found", "Rol no encontrado")
        // Validate that the email is unique
        if (userRepository.existsByEmailAndStatusIsTrue(newUserDto.email)) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Email already exists", "El correo ya existe, por favor elija otro")
        }
        logger.info("Creating the user ${newUserDto.email}")
        // Read file from assets
        val avatarByteArray = assetService.readPhotoToByteArray("avatar.png")
        val avatarThumbnailByteArray = assetService.readPhotoToByteArray("avatar_thumbnail.png")
        // Create the file
        val fileEntity = File()
        fileEntity.fileName = "avatar.png"
        fileEntity.contentType = "image/png"
        fileEntity.fileData = avatarByteArray
        fileEntity.isPicture = true
        fileEntity.thumbnail = avatarThumbnailByteArray
        val savedFile: File = fileRepository.save(fileEntity)
        logger.info("File created with id ${savedFile.fileId}")

        // Create random password from 8 digits
        val password = (10000000..99999999).random().toString()
        // Encrypt the password
        val passwordHash = BCrypt.withDefaults().hashToString(12, password.toCharArray())

        // Create the user
        val userEntity = User()
        userEntity.filePhotoId = savedFile.fileId
        userEntity.email = newUserDto.email.lowercase()
        userEntity.firstName = newUserDto.firstName
        userEntity.lastName = newUserDto.lastName
        userEntity.username = newUserDto.email
        userEntity.phone = newUserDto.phone
        userEntity.description = newUserDto.description
        userEntity.password = passwordHash
        userRepository.save(userEntity)
        logger.info("User created with id ${userEntity.userId}")

        // Create the user role
        val userRoleEntity = UserRole()
        userRoleEntity.userId = userEntity.userId
        userRoleEntity.roleId = newUserDto.roleId
        userRoleRepository.save(userRoleEntity)
        logger.info("User role created with id ${userRoleEntity.userRoleId}")


        // Send the email with the password
        emailService.sendEmail(newUserDto.email, "Bienvenido a la plataforma",
            "Bienvenido ${newUserDto.firstName} ${newUserDto.lastName} a la plataforma del Laboratorio Multimedia.\n" +
                    "Puede acceder a la plataforma en el siguiente enlace: $url\n" +
                    "Se le asignó el rol de ${roleEntity.roleName}.\n" +
                    "Su contraseña es: $password\n" +
                    "Por favor, cambie su contraseña en su primer inicio de sesión.")
    }

    fun updateUser(
        userId: Long,
        profileDto: ProfileDto
    ) {
        // Validate that at least one field is being updated
        if (profileDto.firstName.isBlank() && profileDto.lastName.isBlank() && profileDto.phone.isBlank() && profileDto.description.isBlank()) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: At least one field must be updated", "Al menos un campo debe ser actualizado")
        }
        // Firstname and lastname cannot be blank
        if (profileDto.firstName.isBlank() || profileDto.lastName.isBlank()) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Firstname and lastname cannot be blank", "Nombre y apellido no pueden estar en blanco")
        }
        // Phone must be a number
        if (profileDto.phone.isNotBlank() && !profileDto.phone.matches(Regex("\\AssistantScheduleDto+"))) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Phone must be a number", "El teléfono debe ser un número")
        }
        logger.info("Updating the user with id $userId")
        // Get the user
        val userEntity: User = userRepository.findByUserIdAndStatusIsTrue(userId)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: User not found", "Usuario no encontrado")
        // Update the user
        userEntity.firstName = profileDto.firstName
        userEntity.lastName = profileDto.lastName
        userEntity.phone = profileDto.phone
        userEntity.description = profileDto.description
        userRepository.save(userEntity)
    }

    fun deleteUser(userId: Long) {
        logger.info("Deleting the user with id $userId")
        // Get the user
        val userEntity: User = userRepository.findByUserIdAndStatusIsTrue(userId)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: User not found", "Usuario no encontrado")
        // Validations for the user
        // Check if the user is a project owner, project moderator, project member or task assignee
        if (projectOwnerRepository.findAllByUserIdAndStatusIsTrue(userId).isNotEmpty() ||
            projectModeratorRepository.findAllByUserIdAndStatusIsTrue(userId).isNotEmpty() ||
            projectMemberRepository.findAllByUserIdAndStatusIsTrue(userId).isNotEmpty() ||
            taskAssigneeRepository.findAllByUserIdAndStatusIsTrue(userId).isNotEmpty()
        ) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: User is assigned to a project or task", "El usuario está asignado a un proyecto o tarea")
        }

        // Change the status to false
        userEntity.status = false
        userRepository.save(userEntity)
    }

    // Profile picture
    fun getProfilePicture(userId: Long): FileDto {
        logger.info("Getting the profile picture of $userId")
        // Get the user
        val userEntity: User = userRepository.findByUserIdAndStatusIsTrue(userId)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: User not found", "Usuario no encontrado")
        return fileService.getPicture(userEntity.filePhotoId)
    }

    fun uploadProfilePicture(
        userId: Long,
        file: MultipartFile
    ) {
        logger.info("Uploading the profile picture of $userId")
        // Get the user
        val userEntity: User = userRepository.findByUserIdAndStatusIsTrue(userId)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: User not found", "Usuario no encontrado")
        // Update the same file
        fileService.overwritePicture(file, userEntity.filePhotoId)
        // Update the thumbnail
        fileService.overwriteThumbnail(file, userEntity.filePhotoId)
    }

    fun getRolesByUserId(userId: Long): List<RoleDto> {
        logger.info("Getting roles for user with id $userId")
        // Validate that the user exists
        userRepository.findByUserIdAndStatusIsTrue(userId)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: User not found", "Usuario no encontrado")
        // Get the roles
        val roleEntities = roleRepository.findAllByUserId(userId)
        return roleEntities.map { RoleMapper.entityToDto(it) }
    }

    fun addRolesToUser(
        userId: Long,
        roleIds: List<Long>
    ) {
        logger.info("Adding roles to the user with id $userId")
        // Validate roleIds are unique
        if (roleIds.distinct().size != roleIds.size) throw EtnException(HttpStatus.BAD_REQUEST, "Error: Duplicate roles are not allowed", "No se permiten roles duplicados")
        // Get the user
        userRepository.findByUserIdAndStatusIsTrue(userId)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: User not found", "Usuario no encontrado")
        // Validate that the roles exist
        val roleEntities = roleRepository.findAllByRoleIds(roleIds)
        if (roleEntities.size != roleIds.size) throw EtnException(HttpStatus.NOT_FOUND, "Error: Role not found", "Al menos un rol no fue encontrado")
        // Delete previous roles changing the status to false
        logger.info("Deleting previous roles")
        val userRoleEntities = userRoleRepository.findAllByUserIdAndStatusIsTrue(userId)
        userRoleEntities.forEach {
            it.status = false
            userRoleRepository.save(it)
        }
        // Add the new roles
        logger.info("Adding new roles")
        roleEntities.forEach {
            val userRoleEntity = UserRole()
            userRoleEntity.userId = userId.toInt()
            userRoleEntity.roleId = it.roleId
            userRoleRepository.save(userRoleEntity)
        }
    }

    fun getProfilePictureThumbnail(userId: Long): FileDto {
        logger.info("Getting the profile picture thumbnail of $userId")
        // Get the user
        val userEntity: User = userRepository.findByUserIdAndStatusIsTrue(userId)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: User not found", "Usuario no encontrado")
        return fileService.getPicture(userEntity.filePhotoId)
    }
}