package bo.edu.umsa.backend.service

import bo.edu.umsa.backend.dto.RoleDto
import bo.edu.umsa.backend.dto.PermissionDto
import bo.edu.umsa.backend.entity.Role
import bo.edu.umsa.backend.entity.RolePermission
import bo.edu.umsa.backend.exception.EtnException
import bo.edu.umsa.backend.mapper.RoleMapper
import bo.edu.umsa.backend.mapper.PermissionMapper
import bo.edu.umsa.backend.repository.RoleRepository
import bo.edu.umsa.backend.repository.RolePermissionRepository
import bo.edu.umsa.backend.repository.PermissionRepository
import bo.edu.umsa.backend.repository.UserRoleRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.util.*

@Service
class RoleService @Autowired constructor(
    private val roleRepository: RoleRepository,
    private val permissionRepository: PermissionRepository,
    private val rolePermissionRepository: RolePermissionRepository,
    private val userRoleRepository: UserRoleRepository
) {
    companion object {
        private val logger = LoggerFactory.getLogger(RoleService::class.java)
    }

    fun getRoles(): List<RoleDto> {
        logger.info("Getting roles")
        val roleEntities = roleRepository.findAllByStatusIsTrueOrderByRoleId()
        return roleEntities.map { RoleMapper.entityToDto(it) }
    }

    fun createRole(roleDto: RoleDto) {
        // Validate that the fields are not empty
        if (roleDto.roleName.isBlank() || roleDto.roleDescription.isBlank()) throw EtnException(HttpStatus.BAD_REQUEST, "Error: Empty fields", "Al menos un campo está vacío")
        logger.info("Creating role")
        val roleEntity = Role()
        roleEntity.roleName = roleDto.roleName.uppercase(Locale.getDefault())
        roleEntity.roleDescription = roleDto.roleDescription
        roleRepository.save(roleEntity)
    }

    fun updateRole(
        roleId: Long,
        roleDto: RoleDto
    ) {
        // Validate that the fields are not empty
        if (roleDto.roleName.isBlank() || roleDto.roleDescription.isBlank()) throw EtnException(HttpStatus.BAD_REQUEST, "Error: Empty fields", "Al menos un campo está vacío")
        logger.info("Updating role")
        val roleEntity = roleRepository.findByRoleIdAndStatusIsTrue(roleId)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: Role not found", "Rol no encontrado")
        roleEntity.roleName = roleDto.roleName.uppercase(Locale.getDefault())
        roleEntity.roleDescription = roleDto.roleDescription
        roleRepository.save(roleEntity)
    }

    fun deleteRole(roleId: Long) {
        logger.info("Deleting role")
        val roleEntity = roleRepository.findByRoleIdAndStatusIsTrue(roleId)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: Role not found", "Rol no encontrado")
        // Validate that the role has no permissions nor users assigned
        val rolePermissionEntities = rolePermissionRepository.findAllByRoleIdAndStatusIsTrue(roleId)
        if (rolePermissionEntities.isNotEmpty()) throw EtnException(HttpStatus.BAD_REQUEST, "Error: Role has permissions assigned", "El rol tiene permisos asignados")
        val userRoleEntities = userRoleRepository.findAllByRoleIdAndStatusIsTrue(roleId)
        if (userRoleEntities.isNotEmpty()) throw EtnException(HttpStatus.BAD_REQUEST, "Error: Role has users assigned", "El rol tiene usuarios asignados")
        roleEntity.status = false
        roleRepository.save(roleEntity)
    }

    fun getPermissionsByRoleId(roleId: Long): List<PermissionDto> {
        logger.info("Getting permissions for role with id $roleId")
        // Validate that the role exists
        roleRepository.findByRoleIdAndStatusIsTrue(roleId)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: Role not found", "Rol no encontrado")
        // Get the roles
        val permissionEntities = permissionRepository.findAllByRoleId(roleId)
        return permissionEntities.map { PermissionMapper.entityToDto(it) }
    }

    fun addPermissionsToRole(
        roleId: Long,
        permissionIds: List<Long>
    ) {
        logger.info("Adding permissions to role with id $roleId")
        // Validate permissions are not duplicated
        if (permissionIds.size != permissionIds.distinct().size) throw EtnException(HttpStatus.BAD_REQUEST, "Error: Duplicate permissions are not allowed", "No se permiten permisos duplicados")
        // Validate that the role exists
        roleRepository.findByRoleIdAndStatusIsTrue(roleId)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: Role not found", "Rol no encontrado")
        // Validate that the permissions exist
        val permissionEntities = permissionRepository.findAllByPermissionIds(permissionIds)
        if (permissionEntities.size != permissionIds.size) throw EtnException(HttpStatus.NOT_FOUND, "Error: Permission not found", "Al menos un permiso no fue encontrado")
        // Delete previous permissions changing the status to false
        logger.info("Deleting previous permissions")
        val rolePermissionEntities = rolePermissionRepository.findAllByRoleIdAndStatusIsTrue(roleId)
        rolePermissionEntities.forEach {
            it.status = false
            rolePermissionRepository.save(it)
        }
        // Add the new roles
        logger.info("Adding new roles")
        permissionEntities.forEach {
            val rolePermissionEntity = RolePermission()
            rolePermissionEntity.roleId = roleId.toInt()
            rolePermissionEntity.permissionId = it.permissionId
            rolePermissionRepository.save(rolePermissionEntity)
        }
    }
}