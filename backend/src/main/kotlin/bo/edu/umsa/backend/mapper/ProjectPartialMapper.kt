package bo.edu.umsa.backend.mapper

import bo.edu.umsa.backend.dto.ProjectPartialDto
import bo.edu.umsa.backend.entity.Project

class ProjectPartialMapper {
    companion object{
        fun entityToDto(project: Project):ProjectPartialDto {
            return ProjectPartialDto(
                projectId = project.projectId,
                projectName = project.projectName,
                projectDescription = project.projectDescription
            )
        }
    }
}