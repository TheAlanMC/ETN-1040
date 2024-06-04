package bo.edu.umsa.backend.service

import at.favre.lib.crypto.bcrypt.BCrypt
import bo.edu.umsa.backend.dto.*
import bo.edu.umsa.backend.entity.File
import bo.edu.umsa.backend.entity.User
import bo.edu.umsa.backend.entity.UserGroup
import bo.edu.umsa.backend.exception.EtnException
import bo.edu.umsa.backend.mapper.GroupMapper
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

@Service
class UserService @Autowired constructor(
    private val assetService: AssetService,
    private val userRepository: UserRepository,
    private val groupRepository: GroupRepository,
    private val fileRepository: FileRepository,
    private val userGroupRepository: UserGroupRepository,
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
        // Validate that the group id is greater than 0
        if (newUserDto.groupId <= 0) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Group was not selected", "Rol no seleccionado")
        }
        // Validate the email format
        if (!newUserDto.email.matches(Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}\$"))) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Invalid email format", "Formato de correo inválido")
        }
        // Phone must be a number
        if (newUserDto.phone.isNotBlank() && !newUserDto.phone.matches(Regex("\\d+"))) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Phone must be a number", "El teléfono debe ser un número")
        }
        // Validate that the group exists
        val groupEntity = groupRepository.findByGroupIdAndStatusIsTrue(newUserDto.groupId.toLong())
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: Group not found", "Grupo no encontrado")
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
        userEntity.email = newUserDto.email
        userEntity.firstName = newUserDto.firstName
        userEntity.lastName = newUserDto.lastName
        userEntity.username = newUserDto.email
        userEntity.phone = newUserDto.phone
        userEntity.description = newUserDto.description
        userEntity.password = passwordHash
        userRepository.save(userEntity)
        logger.info("User created with id ${userEntity.userId}")

        // Create the user group
        val userGroupEntity = UserGroup()
        userGroupEntity.userId = userEntity.userId
        userGroupEntity.groupId = newUserDto.groupId
        userGroupRepository.save(userGroupEntity)
        logger.info("User group created with id ${userGroupEntity.userGroupId}")


        // Send the email with the password
        emailService.sendEmail(newUserDto.email, "Bienvenido a la plataforma",
            "Bienvenido ${newUserDto.firstName} ${newUserDto.lastName} a la plataforma del Laboratorio Multimedia.\n" +
                    "Puede acceder a la plataforma en el siguiente enlace: $url\n" +
                    "Se le asignó el rol de ${groupEntity.groupName}.\n" +
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
        if (profileDto.phone.isNotBlank() && !profileDto.phone.matches(Regex("\\d+"))) {
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

    fun getGroupsByUserId(userId: Long): List<GroupDto> {
        logger.info("Getting groups for user with id $userId")
        // Validate that the user exists
        userRepository.findByUserIdAndStatusIsTrue(userId)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: User not found", "Usuario no encontrado")
        // Get the groups
        val groupEntities = groupRepository.findAllByUserId(userId)
        return groupEntities.map { GroupMapper.entityToDto(it) }
    }

    fun addGroupsToUser(
        userId: Long,
        groupIds: List<Long>
    ) {
        logger.info("Adding groups to the user with id $userId")
        // Get the user
        userRepository.findByUserIdAndStatusIsTrue(userId)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: User not found", "Usuario no encontrado")
        // Validate that the groups exist
        val groupEntities = groupRepository.findAllByGroupIds(groupIds)
        if (groupEntities.size != groupIds.size) throw EtnException(HttpStatus.NOT_FOUND, "Error: Group not found", "Al menos un grupo no fue encontrado")
        // Delete previous roles changing the status to false
        logger.info("Deleting previous groups")
        val userGroupEntities = userGroupRepository.findAllByUserIdAndStatusIsTrue(userId)
        userGroupEntities.forEach {
            it.status = false
            userGroupRepository.save(it)
        }
        // Add the new groups
        logger.info("Adding new groups")
        groupEntities.forEach {
            val userGroupEntity = UserGroup()
            userGroupEntity.userId = userId.toInt()
            userGroupEntity.groupId = it.groupId
            userGroupRepository.save(userGroupEntity)
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