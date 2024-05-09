package bo.edu.umsa.backend.repository

import bo.edu.umsa.backend.entity.ProjectMember
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectMemberRepository: JpaRepository<ProjectMember, Long> {
}
