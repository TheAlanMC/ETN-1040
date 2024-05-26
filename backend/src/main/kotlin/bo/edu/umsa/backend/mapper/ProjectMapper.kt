package bo.edu.umsa.backend.mapper

import bo.edu.umsa.backend.dto.ProjectDto
import bo.edu.umsa.backend.entity.Project

class ProjectMapper {
    companion object {
        fun entityToDto(project: Project): ProjectDto {
            return ProjectDto(
                projectId = project.projectId,
                projectName = project.projectName,
                projectDescription = project.projectDescription,
                projectObjective = project.projectObjective,
                projectCloseMessage = project.projectCloseMessage,
                projectDateFrom = project.projectDateFrom,
                projectDateTo = project.projectDateTo,
                projectEndDate = project.projectEndDate,
                projectOwners = project.projectOwners?.filter { it.status }?.map { UserPartialMapper.entityToDto(it.user!!) } ?: emptyList(),
                projectModerators = project.projectModerators?.filter { it.status }?.map { UserPartialMapper.entityToDto(it.user!!) } ?: emptyList(),
                projectMembers = project.projectMembers?.filter { it.status }?.map { UserPartialMapper.entityToDto(it.user!!) } ?: emptyList(),
                finishedTasks = project.tasks?.filter { it.status && it.taskEndDate != null }?.size ?: 0,
                totalTasks = project.tasks?.filter { it.status }?.size ?: 0,
            )
        }
    }
}