package bo.edu.umsa.backend.controller

import bo.edu.umsa.backend.dto.RoleDto
import bo.edu.umsa.backend.dto.ResponseDto
import bo.edu.umsa.backend.dto.PermissionDto
import bo.edu.umsa.backend.service.RoleService
import bo.edu.umsa.backend.util.AuthUtil
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/roles")
class RoleController @Autowired constructor(private val roleService: RoleService) {

    companion object {
        private val logger = LoggerFactory.getLogger(RoleController::class.java.name)
    }

    @GetMapping
    fun getRoles(): ResponseEntity<ResponseDto<List<RoleDto>>> {
        logger.info("Starting the API call to get the roles")
        logger.info("GET /api/v1/roles")
        AuthUtil.verifyAuthTokenHasPermission("GESTIONAR ROLES Y PERMISOS")
        val roles: List<RoleDto> = roleService.getRoles()
        logger.info("Success: Roles retrieved")
        return ResponseEntity(ResponseDto(true, "Roles recuperados", roles), HttpStatus.OK)
    }

    @PostMapping
    fun createRole(@RequestBody roleDto: RoleDto): ResponseEntity<ResponseDto<Nothing>> {
        logger.info("Starting the API call to create the role")
        logger.info("POST /api/v1/roles")
        AuthUtil.verifyAuthTokenHasPermission("GESTIONAR ROLES Y PERMISOS")
        roleService.createRole(roleDto)
        logger.info("Success: Role created")
        return ResponseEntity(ResponseDto(true, "Rol creado", null), HttpStatus.CREATED)
    }

    @PutMapping("/{roleId}")
    fun updateRole(
        @PathVariable roleId: Long,
        @RequestBody roleDto: RoleDto
    ): ResponseEntity<ResponseDto<Nothing>> {
        logger.info("Starting the API call to update the role")
        logger.info("PUT /api/v1/roles/{roleId}")
        AuthUtil.verifyAuthTokenHasPermission("GESTIONAR ROLES Y PERMISOS")
        roleService.updateRole(roleId, roleDto)
        logger.info("Success: Role updated")
        return ResponseEntity(ResponseDto(true, "Rol actualizado", null), HttpStatus.OK)
    }

    @DeleteMapping("/{roleId}")
    fun deleteRole(@PathVariable roleId: Long): ResponseEntity<ResponseDto<Nothing>> {
        logger.info("Starting the API call to delete the role")
        logger.info("DELETE /api/v1/roles/{roleId}")
        AuthUtil.verifyAuthTokenHasPermission("GESTIONAR ROLES Y PERMISOS")
        roleService.deleteRole(roleId)
        logger.info("Success: Role deleted")
        return ResponseEntity(ResponseDto(true, "Rol eliminado", null), HttpStatus.OK)
    }

    @GetMapping("/{roleId}/permissions")
    fun getPermissionsByRoleId(@PathVariable roleId: Long): ResponseEntity<ResponseDto<List<PermissionDto>>> {
        logger.info("Starting the API call to get the permissions by role id")
        logger.info("GET /api/v1/roles/{roleId}/permissions")
        AuthUtil.verifyAuthTokenHasPermission("GESTIONAR ROLES Y PERMISOS")
        val permissions: List<PermissionDto> = roleService.getPermissionsByRoleId(roleId)
        logger.info("Success: Permissions retrieved")
        return ResponseEntity(ResponseDto(true, "Permisos recuperados", permissions), HttpStatus.OK)
    }

    @PostMapping("/{roleId}/permissions")
    fun addPermissionsToRole(
        @PathVariable roleId: Long,
        @RequestBody permissionIds: Map<String, List<Long>>
    ): ResponseEntity<ResponseDto<Nothing>> {
        logger.info("Starting the API call to add permissions to role")
        logger.info("POST /api/v1/roles/{roleId}/permissions")
        AuthUtil.verifyAuthTokenHasPermission("GESTIONAR ROLES Y PERMISOS")
        roleService.addPermissionsToRole(roleId, permissionIds["permissionIds"]!!)
        logger.info("Success: Permissions added to role")
        return ResponseEntity(ResponseDto(true, "Permisos agregados al rol", null), HttpStatus.CREATED)
    }
}