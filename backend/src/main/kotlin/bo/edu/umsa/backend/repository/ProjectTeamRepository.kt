package bo.edu.umsa.backend.repository

import bo.edu.umsa.backend.entity.ProjectTeam
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectTeamRepository: JpaRepository<ProjectTeam, Long> {
}
