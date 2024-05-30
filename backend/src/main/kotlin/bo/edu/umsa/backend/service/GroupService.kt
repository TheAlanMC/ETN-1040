package bo.edu.umsa.backend.service

import bo.edu.umsa.backend.dto.GroupDto
import bo.edu.umsa.backend.dto.RoleDto
import bo.edu.umsa.backend.entity.Group
import bo.edu.umsa.backend.entity.GroupRole
import bo.edu.umsa.backend.exception.EtnException
import bo.edu.umsa.backend.mapper.GroupMapper
import bo.edu.umsa.backend.mapper.RoleMapper
import bo.edu.umsa.backend.repository.GroupRepository
import bo.edu.umsa.backend.repository.GroupRoleRepository
import bo.edu.umsa.backend.repository.RoleRepository
import bo.edu.umsa.backend.repository.UserGroupRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.util.*

@Service
class GroupService @Autowired constructor(
    private val groupRepository: GroupRepository,
    private val roleRepository: RoleRepository,
    private val groupRoleRepository: GroupRoleRepository,
    private val userGroupRepository: UserGroupRepository
) {
    companion object {
        private val logger = LoggerFactory.getLogger(GroupService::class.java)
    }

    fun getGroups(): List<GroupDto> {
        logger.info("Getting groups")
        val groupEntities = groupRepository.findAllByStatusIsTrueOrderByGroupId()
        return groupEntities.map { GroupMapper.entityToDto(it) }
    }

    fun createGroup(groupDto: GroupDto) {
        // Validate that the fields are not empty
        if (groupDto.groupName.isBlank() || groupDto.groupDescription.isBlank()) throw EtnException(HttpStatus.BAD_REQUEST, "Error: Empty fields", "Al menos un campo está vacío")
        logger.info("Creating group")
        val groupEntity = Group()
        groupEntity.groupName = groupDto.groupName.uppercase(Locale.getDefault())
        groupEntity.groupDescription = groupDto.groupDescription
        groupRepository.save(groupEntity)
    }

    fun updateGroup(
        groupId: Long,
        groupDto: GroupDto
    ) {
        // Validate that the fields are not empty
        if (groupDto.groupName.isBlank() || groupDto.groupDescription.isBlank()) throw EtnException(HttpStatus.BAD_REQUEST, "Error: Empty fields", "Al menos un campo está vacío")
        logger.info("Updating group")
        val groupEntity = groupRepository.findByGroupIdAndStatusIsTrue(groupId)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: Group not found", "Rol no encontrado")
        groupEntity.groupName = groupDto.groupName.uppercase(Locale.getDefault())
        groupEntity.groupDescription = groupDto.groupDescription
        groupRepository.save(groupEntity)
    }

    fun deleteGroup(groupId: Long) {
        logger.info("Deleting group")
        val groupEntity = groupRepository.findByGroupIdAndStatusIsTrue(groupId)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: Group not found", "Rol no encontrado")
        // Validate that the group has no roles nor users assigned
        val groupRoleEntities = groupRoleRepository.findAllByGroupIdAndStatusIsTrue(groupId)
        if (groupRoleEntities.isNotEmpty()) throw EtnException(HttpStatus.BAD_REQUEST, "Error: Group has roles assigned", "El rol tiene permisos asignados")
        val userGroupEntities = userGroupRepository.findAllByGroupIdAndStatusIsTrue(groupId)
        if (userGroupEntities.isNotEmpty()) throw EtnException(HttpStatus.BAD_REQUEST, "Error: Group has users assigned", "El rol tiene usuarios asignados")
        groupEntity.status = false
        groupRepository.save(groupEntity)
    }

    fun getRolesByGroupId(groupId: Long): List<RoleDto> {
        logger.info("Getting roles for group with id $groupId")
        // Validate that the group exists
        groupRepository.findByGroupIdAndStatusIsTrue(groupId)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: Group not found", "Rol no encontrado")
        // Get the roles
        val roleEntities = roleRepository.findAllByGroupId(groupId)
        return roleEntities.map { RoleMapper.entityToDto(it) }
    }

    fun addRolesToGroup(
        groupId: Long,
        roleIds: List<Long>
    ) {
        logger.info("Adding roles to group with id $groupId")
        // Validate that the group exists
        groupRepository.findByGroupIdAndStatusIsTrue(groupId)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: Group not found", "Grupo no encontrado")
        // Validate that the roles exist
        val roleEntities = roleRepository.findAllByRoleIds(roleIds)
        if (roleEntities.size != roleIds.size) throw EtnException(HttpStatus.NOT_FOUND, "Error: Role not found", "Al menos un rol no fue encontrado")
        // Delete previous roles changing the status to false
        logger.info("Deleting previous roles")
        val groupRoleEntities = groupRoleRepository.findAllByGroupIdAndStatusIsTrue(groupId)
        groupRoleEntities.forEach {
            it.status = false
            groupRoleRepository.save(it)
        }
        // Add the new roles
        logger.info("Adding new roles")
        roleEntities.forEach {
            val groupRoleEntity = GroupRole()
            groupRoleEntity.groupId = groupId.toInt()
            groupRoleEntity.roleId = it.roleId
            groupRoleRepository.save(groupRoleEntity)
        }
    }
}