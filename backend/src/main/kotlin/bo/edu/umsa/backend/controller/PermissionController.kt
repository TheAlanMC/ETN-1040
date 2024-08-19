package bo.edu.umsa.backend.controller

import bo.edu.umsa.backend.dto.ResponseDto
import bo.edu.umsa.backend.dto.PermissionDto
import bo.edu.umsa.backend.service.PermissionService
import bo.edu.umsa.backend.util.AuthUtil
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/permissions")
class PermissionController @Autowired constructor(private val permissionService: PermissionService) {

    companion object {
        private val logger = LoggerFactory.getLogger(PermissionController::class.java.name)
    }

    @GetMapping
    fun getPermissions(): ResponseEntity<ResponseDto<List<PermissionDto>>> {
        logger.info("Starting the API call to get the permissions")
        logger.info("GET /api/v1/permissions")
        AuthUtil.verifyAuthTokenHasPermission("GESTIONAR ROLES Y PERMISOS")
        val permissions: List<PermissionDto> = permissionService.getPermissions()
        logger.info("Success: Permissions retrieved")
        return ResponseEntity(ResponseDto(true, "Permisos recuperados", permissions), HttpStatus.OK)
    }

}