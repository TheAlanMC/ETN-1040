package bo.edu.umsa.backend.repository

import bo.edu.umsa.backend.entity.Project
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectRepository: JpaRepository<Project, Long> {
}
