package bo.edu.umsa.backend.service

import bo.edu.umsa.backend.dto.RoleDto
import bo.edu.umsa.backend.mapper.RoleMapper
import bo.edu.umsa.backend.repository.RoleRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class RoleService @Autowired constructor(
    private val roleRepository: RoleRepository,
) {
    companion object {
        private val logger = org.slf4j.LoggerFactory.getLogger(RoleService::class.java)
    }

    fun getRoles() : List<RoleDto> {
        logger.info("Getting roles")
        val roleEntities = roleRepository.findAllByStatusIsTrue()
        return roleEntities.map { RoleMapper.entityToDto(it) }
    }

}