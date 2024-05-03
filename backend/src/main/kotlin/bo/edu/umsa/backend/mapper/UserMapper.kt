package bo.edu.umsa.backend.mapper

import bo.edu.umsa.backend.dto.UserDto
import bo.edu.umsa.backend.entity.User


class UserMapper {
    companion object{
        fun entityToDto(user: User): UserDto {
            return UserDto(
                firstName = user.firstName,
                lastName = user.lastName,
                phone = user.phone,
                description = user.description
            )
        }
    }
}