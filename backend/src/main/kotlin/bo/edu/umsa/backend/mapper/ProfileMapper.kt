package bo.edu.umsa.backend.mapper

import bo.edu.umsa.backend.dto.ProfileDto
import bo.edu.umsa.backend.entity.User


class ProfileMapper {
    companion object{
        fun entityToDto(user: User): ProfileDto {
            return ProfileDto(
                firstName = user.firstName,
                lastName = user.lastName,
                phone = user.phone,
                description = user.description,
            )
        }
    }
}