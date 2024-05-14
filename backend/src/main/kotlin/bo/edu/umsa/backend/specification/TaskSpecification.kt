package bo.edu.umsa.backend.specification

import bo.edu.umsa.backend.entity.Task
import bo.edu.umsa.backend.entity.TaskAssignee
import bo.edu.umsa.backend.entity.TaskStatus
import bo.edu.umsa.backend.entity.User
import jakarta.persistence.criteria.Expression
import org.springframework.data.jpa.domain.Specification


class TaskSpecification {
    companion object {

        fun taskKeyword(keyword: String): Specification<Task> {
            return Specification { root, _, cb ->
                cb.or(
                    cb.like(cb.lower(root.get("taskName")), "%${keyword.lowercase()}%"),
                    cb.like(cb.lower(root.get("taskDescription")), "%${keyword.lowercase()}%"),
                    cb.like(cb.lower(root.get("taskPriority")), "%${keyword.lowercase()}%"),
                    cb.like(
                        cb.lower(
                            root.get<Task>("taskStatus").get<TaskStatus>("taskStatusName") as Expression<String>
                        ), "%${keyword.lowercase()}%"
                    ),
                    cb.like(
                        cb.lower(
                            root.get<Task>("taskAssignee").get<TaskAssignee>("user")
                                .get<User>("firstName") as Expression<String>
                        ), "%${keyword.lowercase()}%"
                    ),
                    cb.like(
                        cb.lower(
                            root.get<Task>("taskAssignee").get<TaskAssignee>("user")
                                .get<User>("lastName") as Expression<String>
                        ), "%${keyword.lowercase()}%"
                    ),
                    cb.like(
                        cb.lower(
                            root.get<Task>("taskAssignee").get<TaskAssignee>("user")
                                .get<User>("email") as Expression<String>
                        ), "%${keyword.lowercase()}%"
                    )
                )
            }
        }

        fun projectId(projectId: Int): Specification<Task> {
            return Specification { root, _, cb ->
                cb.equal(root.get<Task>("project").get<Int>("projectId"), projectId)
            }
        }

        fun statusIsTrue(): Specification<Task> {
            return Specification { root, _, cb ->
                cb.equal(root.get<Task>("status"), true)
            }
        }
    }
}