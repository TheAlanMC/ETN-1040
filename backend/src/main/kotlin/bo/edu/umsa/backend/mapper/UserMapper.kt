package bo.edu.umsa.backend.mapper

import bo.edu.umsa.backend.dto.UserDto
import bo.edu.umsa.backend.entity.User


class UserMapper {
    companion object {
        fun entityToDto(user: User): UserDto {
            return UserDto(
                userId = user.userId,
                email = user.email,
                firstName = user.firstName,
                lastName = user.lastName,
                phone = user.phone,
                description = user.description,
                txUser = user.txUser,
                txDate = user.txDate,
                roles = user.userGroups?.filter { userGroup -> userGroup.status }?.mapNotNull { userGroup -> userGroup.group?.groupRoles?.filter { groupRole -> groupRole.status }?.map { groupRole -> groupRole.role } }?.flatten()?.filter { role -> role!!.status }?.distinctBy { role -> role!!.roleId }?.sortedBy { role -> role!!.roleId }?.mapNotNull { role -> role!!.roleName }
                    ?: emptyList(),
                groups = user.userGroups?.filter { it.status }?.sortedBy { it.group?.groupId }?.mapNotNull { it.group?.groupName } ?: emptyList(),
            )
        }
    }
}