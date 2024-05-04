package bo.edu.umsa.backend.mapper

import bo.edu.umsa.backend.dto.RoleDto
import bo.edu.umsa.backend.entity.Role


class RoleMapper {
    companion object {
        fun entityToDto(role: Role): RoleDto {
            return RoleDto(
                roleId = role.roleId,
                roleName = role.roleName,
                roleDescription = role.roleDescription,
            )
        }
    }
}