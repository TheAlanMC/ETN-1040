package bo.edu.umsa.backend.specification

import bo.edu.umsa.backend.entity.Project
import org.springframework.data.jpa.domain.Specification


class ProjectSpecification {
    companion object {

        fun statusIsTrue(): Specification<Project> {
            return Specification { root, _, cb ->
                cb.equal(root.get<Project>("status"), true)
            }
        }
    }
}