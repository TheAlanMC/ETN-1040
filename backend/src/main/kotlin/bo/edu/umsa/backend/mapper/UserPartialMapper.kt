package bo.edu.umsa.backend.mapper

import bo.edu.umsa.backend.dto.UserPartialDto
import bo.edu.umsa.backend.entity.User


class UserPartialMapper {
    companion object {
        fun entityToDto(user: User): UserPartialDto {
            return UserPartialDto(
                userId = user.userId,
                email = user.email,
                firstName = user.firstName,
                lastName = user.lastName,
                phone = user.phone,
                txDate = user.txDate,
                txHost = user.txHost
            )
        }
    }
}