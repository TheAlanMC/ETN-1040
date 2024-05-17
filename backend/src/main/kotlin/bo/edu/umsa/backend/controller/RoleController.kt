package bo.edu.umsa.backend.controller

import bo.edu.umsa.backend.dto.ResponseDto
import bo.edu.umsa.backend.dto.RoleDto
import bo.edu.umsa.backend.service.RoleService
import bo.edu.umsa.backend.util.AuthUtil
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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
        AuthUtil.verifyAuthTokenHasRole("GESTIONAR ROLES Y PERMISOS")
        val roles: List<RoleDto> = roleService.getRoles()
        logger.info("Success: Roles retrieved")
        return ResponseEntity(ResponseDto(true, "Roles recuperados", roles), HttpStatus.OK)
    }

}