package bo.edu.umsa.backend.mapper

import bo.edu.umsa.backend.dto.ProjectPartialDto
import bo.edu.umsa.backend.entity.Project

class ProjectPartialMapper {
    companion object {
        fun entityToDto(project: Project): ProjectPartialDto {
            return ProjectPartialDto(
                projectId = project.projectId,
                projectName = project.projectName,
                projectDateFrom = project.projectDateFrom,
                projectDateTo = project.projectDateTo,
                projectEndDate = project.projectEndDate,
                projectOwners = project.projectOwners?.filter { it.status && it.user?.status == true }?.map { UserPartialMapper.entityToDto(it.user!!) }
                    ?: emptyList(),
                projectModerators = project.projectModerators?.filter { it.status && it.user?.status == true }?.map { UserPartialMapper.entityToDto(it.user!!) }
                    ?: emptyList(),
                projectMembers = project.projectMembers?.filter { it.status && it.user?.status == true }?.map { UserPartialMapper.entityToDto(it.user!!) }
                    ?: emptyList(),
            )
        }
    }
}