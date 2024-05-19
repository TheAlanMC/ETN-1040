package bo.edu.umsa.backend.controller

import bo.edu.umsa.backend.dto.GroupDto
import bo.edu.umsa.backend.dto.ResponseDto
import bo.edu.umsa.backend.dto.RoleDto
import bo.edu.umsa.backend.service.GroupService
import bo.edu.umsa.backend.util.AuthUtil
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/groups")
class GroupController @Autowired constructor(private val groupService: GroupService) {

    companion object {
        private val logger = LoggerFactory.getLogger(GroupController::class.java.name)
    }

    @GetMapping
    fun getGroups(): ResponseEntity<ResponseDto<List<GroupDto>>> {
        logger.info("Starting the API call to get the groups")
        logger.info("GET /api/v1/groups")
        AuthUtil.verifyAuthTokenHasRole("GESTIONAR ROLES Y PERMISOS")
        val groups: List<GroupDto> = groupService.getGroups()
        logger.info("Success: Groups retrieved")
        return ResponseEntity(ResponseDto(true, "Grupos recuperados", groups), HttpStatus.OK)
    }

    @PostMapping
    fun createGroup(@RequestBody groupDto: GroupDto): ResponseEntity<ResponseDto<Nothing>> {
        logger.info("Starting the API call to create the group")
        logger.info("POST /api/v1/groups")
        AuthUtil.verifyAuthTokenHasRole("GESTIONAR ROLES Y PERMISOS")
        groupService.createGroup(groupDto)
        logger.info("Success: Group created")
        return ResponseEntity(ResponseDto(true, "Grupo creado", null), HttpStatus.CREATED)
    }

    @PutMapping("/{groupId}")
    fun updateGroup(
        @PathVariable groupId: Long,
        @RequestBody groupDto: GroupDto
    ): ResponseEntity<ResponseDto<Nothing>> {
        logger.info("Starting the API call to update the group")
        logger.info("PUT /api/v1/groups/{groupId}")
        AuthUtil.verifyAuthTokenHasRole("GESTIONAR ROLES Y PERMISOS")
        groupService.updateGroup(groupId, groupDto)
        logger.info("Success: Group updated")
        return ResponseEntity(ResponseDto(true, "Grupo actualizado", null), HttpStatus.OK)
    }

    @DeleteMapping("/{groupId}")
    fun deleteGroup(@PathVariable groupId: Long): ResponseEntity<ResponseDto<Nothing>> {
        logger.info("Starting the API call to delete the group")
        logger.info("DELETE /api/v1/groups/{groupId}")
        AuthUtil.verifyAuthTokenHasRole("GESTIONAR ROLES Y PERMISOS")
        groupService.deleteGroup(groupId)
        logger.info("Success: Group deleted")
        return ResponseEntity(ResponseDto(true, "Grupo eliminado", null), HttpStatus.OK)
    }

    @GetMapping("/{groupId}/roles")
    fun getRolesByGroupId(@PathVariable groupId: Long): ResponseEntity<ResponseDto<List<RoleDto>>> {
        logger.info("Starting the API call to get the roles by group id")
        logger.info("GET /api/v1/groups/{groupId}/roles")
        AuthUtil.verifyAuthTokenHasRole("GESTIONAR ROLES Y PERMISOS")
        val roles: List<RoleDto> = groupService.getRolesByGroupId(groupId)
        logger.info("Success: Roles retrieved")
        return ResponseEntity(ResponseDto(true, "Roles recuperados", roles), HttpStatus.OK)
    }

    @PostMapping("/{groupId}/roles")
    fun addRolesToGroup(
        @PathVariable groupId: Long,
        @RequestBody roleIds: Map<String, List<Long>>
    ): ResponseEntity<ResponseDto<Nothing>> {
        logger.info("Starting the API call to add roles to group")
        logger.info("POST /api/v1/groups/{groupId}/roles")
        AuthUtil.verifyAuthTokenHasRole("GESTIONAR ROLES Y PERMISOS")
        groupService.addRolesToGroup(groupId, roleIds["roleIds"]!!)
        logger.info("Success: Roles added to group")
        return ResponseEntity(ResponseDto(true, "Roles agregados al grupo", null), HttpStatus.CREATED)
    }
}