package bo.edu.umsa.backend.specification

import bo.edu.umsa.backend.entity.*
import jakarta.persistence.criteria.Expression
import org.springframework.data.jpa.domain.Specification


class ProjectSpecification {
    companion object {

        fun projectKeyword(keyword: String): Specification<Project> {
            return Specification { root, query, cb ->
                query.distinct(true)
                cb.or(
                    cb.like(cb.lower(root.get("projectName")), "%${keyword.lowercase()}%"),
                    cb.like(cb.lower(root.get("projectDescription")), "%${keyword.lowercase()}%"),
//                    cb.like(cb.lower(root.get("projectObjective")), "%${keyword.lowercase()}%"),
//                    cb.like(cb.lower(root.get("projectCloseMessage")), "%${keyword.lowercase()}%"),
//                    cb.like(cb.lower(root.get<Task>("projectOwners").get<TaskAssignee>("user").get<User>("firstName") as Expression<String>), "%${keyword.lowercase()}%"),
//                    cb.like(cb.lower(root.get<Task>("projectOwners").get<TaskAssignee>("user").get<User>("lastName") as Expression<String>), "%${keyword.lowercase()}%"),
//                    cb.like(cb.lower(root.get<Task>("projectOwners").get<TaskAssignee>("user").get<User>("email") as Expression<String>), "%${keyword.lowercase()}%"),
                    cb.like(cb.lower(root.get<Task>("projectModerators").get<TaskAssignee>("user").get<User>("firstName") as Expression<String>), "%${keyword.lowercase()}%"),
                    cb.like(cb.lower(root.get<Task>("projectModerators").get<TaskAssignee>("user").get<User>("lastName") as Expression<String>), "%${keyword.lowercase()}%"),
                    cb.like(cb.lower(root.get<Task>("projectModerators").get<TaskAssignee>("user").get<User>("email") as Expression<String>), "%${keyword.lowercase()}%"),
                    cb.like(cb.lower(root.get<Task>("projectMembers").get<TaskAssignee>("user").get<User>("firstName") as Expression<String>), "%${keyword.lowercase()}%"),
                    cb.like(cb.lower(root.get<Task>("projectMembers").get<TaskAssignee>("user").get<User>("lastName") as Expression<String>), "%${keyword.lowercase()}%"),
                    cb.like(cb.lower(root.get<Task>("projectMembers").get<TaskAssignee>("user").get<User>("email") as Expression<String>), "%${keyword.lowercase()}%"),
                )
            }
        }

        fun statusIsTrue(): Specification<Project> {
            return Specification { root, _, cb ->
                cb.equal(root.get<Project>("status"), true)
            }
        }
    }
}