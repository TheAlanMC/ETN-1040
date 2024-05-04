package bo.edu.umsa.backend.service

import at.favre.lib.crypto.bcrypt.BCrypt
import bo.edu.umsa.backend.dto.FileDto
import bo.edu.umsa.backend.dto.NewUserDto
import bo.edu.umsa.backend.dto.ProfileDto
import bo.edu.umsa.backend.dto.UserDto
import bo.edu.umsa.backend.entity.File
import bo.edu.umsa.backend.entity.User
import bo.edu.umsa.backend.entity.UserGroup
import bo.edu.umsa.backend.exception.EtnException
import bo.edu.umsa.backend.mapper.UserMapper
import bo.edu.umsa.backend.repository.*
import bo.edu.umsa.backend.specification.UserSpecification
import org.springframework.beans.factory.annotation.Autowired
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
    private val emailService: EmailService
){
    companion object {
        private val logger = org.slf4j.LoggerFactory.getLogger(UserService::class.java)
    }

    fun getUsers(
        sortBy: String,
        sortType: String,
        page: Int,
        size: Int,
        keyword: String?
    ): Page<UserDto> {
        logger.info("Getting users")
        // Pagination and sorting
        val pageable: Pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortType), sortBy))
        var specification: Specification<User> = Specification.where(null)
        specification = specification.and(specification.and(UserSpecification.statusIsTrue()))
        // Search by keyword
        if (!keyword.isNullOrEmpty() && keyword.isNotBlank()) {
            specification = specification.and(specification.and(UserSpecification.kcUserKeyword(keyword)))
        }
        val userEntities: Page<User> = userRepository.findAll(specification, pageable)
        return userEntities.map { UserMapper.entityToDto(it) }
    }

    fun createUser(newUserDto: NewUserDto) {
        // Validate that at least this fields are not blank:
        if (newUserDto.email.isBlank() || newUserDto.firstName.isBlank() || newUserDto.lastName.isBlank()) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: At least one required field is blank","Al menos un campo requerido está en blanco")
        }
        // Validate that the group exists
        val groupEntity =groupRepository.findByGroupIdAndStatusIsTrue(newUserDto.groupId.toLong())
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: Group not found","Grupo no encontrado")
        // Validate that the email is unique
        if (userRepository.existsByEmailAndStatusIsTrue(newUserDto.email)) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Email already exists","El correo ya existe")
        }
        logger.info("Creating the user ${newUserDto.email}")
        // Read file from assets
        val avatarByteArray = assetService.readPhotoToByteArray("avatar.png")
        // Create the file
        val fileEntity = File()
        fileEntity.filename = "avatar.png"
        fileEntity.contentType = "image/png"
        fileEntity.fileData = avatarByteArray
        val savedFile: File = fileRepository.save(fileEntity)
        logger.info("File created with id ${savedFile.fileId}")

        // Create random password from 8 digits
        val password = (10000000..99999999).random().toString()
        // Encrypt the password
        val passwordHash = BCrypt.withDefaults().hashToString(12, password.toCharArray())

        // Create the user
        val userEntity: User = User()
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
                    "Se le asignó el rol de ${groupEntity.groupName}.\n" +
                    "Su contraseña es: $password\n" +
                    "Por favor, cambie su contraseña en su primer inicio de sesión."
        )
    }

    fun getUserById(userId: Long): UserDto {
        logger.info("Getting the user with id $userId")
        // Get the user
        val userEntity: User = userRepository.findByUserIdAndStatusIsTrue(userId)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: User not found","Usuario no encontrado")
        return UserMapper.entityToDto(userEntity)
    }

    fun updateUser(userId: Long, profileDto: ProfileDto) {
        // Validate that at least one field is being updated
        if (profileDto.firstName.isBlank() && profileDto.lastName.isBlank() && profileDto.phone.isBlank() && profileDto.description.isBlank()) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: At least one field must be updated","Al menos un campo debe ser actualizado")
        }
        // Firstname and lastname cannot be blank
        if (profileDto.firstName.isBlank() || profileDto.lastName.isBlank()) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Firstname and lastname cannot be blank","Nombre y apellido no pueden estar en blanco")
        }
        logger.info("Updating the user with id $userId")
        // Get the user
        val userEntity: User = userRepository.findByUserIdAndStatusIsTrue(userId)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: User not found","Usuario no encontrado")
        // Update the user
        userEntity.firstName = profileDto.firstName
        userEntity.lastName = profileDto.lastName
        userEntity.phone = profileDto.phone
        userEntity.description = profileDto.description
        userRepository.save(userEntity)
    }

    // Profile picture
    fun getProfilePicture(userId: Long): FileDto {
        logger.info("Getting the profile picture of $userId")
        // Get the user
        val userEntity: User = userRepository.findByUserIdAndStatusIsTrue(userId)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: User not found","Usuario no encontrado")
        return  fileService.getFile(userEntity.filePhotoId.toLong())
    }

    fun uploadProfilePicture(userId: Long, file: MultipartFile) {
        logger.info("Uploading the profile picture of $userId")
        // Get the user
        val userEntity: User = userRepository.findByUserIdAndStatusIsTrue(userId)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: User not found","Usuario no encontrado")
        // Update the same file
        fileService.overwriteFile(file, userEntity.filePhotoId.toLong())
    }

}