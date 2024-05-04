package bo.edu.umsa.backend.mapper

import bo.edu.umsa.backend.dto.UserDto
import bo.edu.umsa.backend.entity.User


class UserMapper {
    companion object{
        fun entityToDto(user: User, roles: List<String> = emptyList(), groups: List<String> = emptyList()): UserDto {
            return UserDto(
                userId = user.userId,
                email = user.email,
                firstName = user.firstName,
                lastName = user.lastName,
                phone = user.phone,
                description = user.description,
                txUser = user.txUser,
                txDate = user.txDate,
                roles = roles,
                groups = groups
            )
        }
    }
}