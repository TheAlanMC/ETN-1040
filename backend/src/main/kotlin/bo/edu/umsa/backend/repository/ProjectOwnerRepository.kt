package bo.edu.umsa.backend.repository

import bo.edu.umsa.backend.entity.ProjectOwner
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectOwnerRepository: JpaRepository<ProjectOwner, Long> {}
