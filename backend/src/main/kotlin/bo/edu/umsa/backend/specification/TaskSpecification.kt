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
            return Specification { root, query, cb ->
                query.distinct(true)
                cb.or(
                    cb.like(cb.lower(root.get("taskName")), "%${keyword.lowercase()}%"),
                    cb.like(cb.lower(root.get("taskDescription")), "%${keyword.lowercase()}%"),
                    cb.like(
                        cb.lower(
                            root.get<Task>("taskStatus").get<TaskStatus>("taskStatusName") as Expression<String>
                        ), "%${keyword.lowercase()}%"
                    ),
                    cb.like(
                        cb.lower(
                            root.get<Task>("taskAssignees").get<TaskAssignee>("user")
                                .get<User>("firstName") as Expression<String>
                        ), "%${keyword.lowercase()}%"
                    ),
                    cb.like(
                        cb.lower(
                            root.get<Task>("taskAssignees").get<TaskAssignee>("user")
                                .get<User>("lastName") as Expression<String>
                        ), "%${keyword.lowercase()}%"
                    ),
                    cb.like(
                        cb.lower(
                            root.get<Task>("taskAssignees").get<TaskAssignee>("user")
                                .get<User>("email") as Expression<String>
                        ), "%${keyword.lowercase()}%"
                    )
                )
            }
        }

        fun taskAssignee(userId: Int): Specification<Task> {
            return Specification { root, _, cb ->
                cb.equal(root.get<Task>("taskAssignees").get<TaskAssignee>("userId"), userId)
            }
        }

        fun taskStatus(taskStatus: String): Specification<Task> {
            return Specification { root, _, cb ->
                cb.equal(root.get<Task>("taskStatus").get<TaskStatus>("taskStatusName"), taskStatus)
            }
        }

        fun taskStatuses(taskStatuses: List<String>): Specification<Task> {
            return Specification { root, _, cb ->
                cb.`in`(root.get<Any>("taskStatus").get<Any>("taskStatusName")).value(taskStatuses)
            }
        }


        fun taskPriority(taskPriority: String): Specification<Task> {
            return Specification { root, _, cb ->
                cb.equal(root.get<Task>("taskPriority"), taskPriority)
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