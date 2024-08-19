package bo.edu.umsa.backend.service

import bo.edu.umsa.backend.dto.PermissionDto
import bo.edu.umsa.backend.mapper.PermissionMapper
import bo.edu.umsa.backend.repository.PermissionRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PermissionService @Autowired constructor(
    private val permissionRepository: PermissionRepository,
) {
    companion object {
        private val logger = LoggerFactory.getLogger(PermissionService::class.java)
    }

    fun getPermissions(): List<PermissionDto> {
        logger.info("Getting permissions")
        val permissionEntities = permissionRepository.findAllByStatusIsTrueOrderByPermissionId()
        return permissionEntities.map { PermissionMapper.entityToDto(it) }
    }

}