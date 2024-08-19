package bo.edu.umsa.backend.mapper

import bo.edu.umsa.backend.dto.PermissionDto
import bo.edu.umsa.backend.entity.Permission


class PermissionMapper {
    companion object {
        fun entityToDto(permission: Permission): PermissionDto {
            return PermissionDto(
                permissionId = permission.permissionId,
                permissionName = permission.permissionName,
                permissionDescription = permission.permissionDescription,
            )
        }
    }
}