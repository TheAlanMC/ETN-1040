package bo.edu.umsa.backend.mapper

import bo.edu.umsa.backend.dto.ProjectDto
import bo.edu.umsa.backend.entity.Project

class ProjectMapper {
    companion object{
        fun entityToDto(project: Project):ProjectDto {
            return ProjectDto(
                projectId = project.projectId,
                projectName = project.projectName,
                projectDescription = project.projectDescription,
                dateFrom = project.dateFrom,
                dateTo = project.dateTo,
                projectOwnerIds = project.projectOwners?.mapNotNull { it.user?.userId } ?: emptyList(),
                projectModeratorIds = project.projectModerators?.mapNotNull { it.user?.userId } ?: emptyList(),
                projectMemberIds = project.projectMembers?.mapNotNull { it.user?.userId } ?: emptyList()
            )
        }
    }
}