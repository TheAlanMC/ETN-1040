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
                permissions = user.userRoles?.filter { userRole -> userRole.status }?.mapNotNull { userRole -> userRole.role?.rolePermissions?.filter { rolePermission -> rolePermission.status }?.map { rolePermission -> rolePermission.permission } }?.flatten()?.filter { permission -> permission!!.status }?.distinctBy { permission -> permission!!.permissionId }?.sortedBy { permission -> permission!!.permissionId }?.mapNotNull { permission -> permission!!.permissionName }
                    ?: emptyList(),
                roles = user.userRoles?.filter { it.status }?.sortedBy { it.role?.roleId }?.mapNotNull { it.role?.roleName } ?: emptyList(),
            )
        }
    }
}