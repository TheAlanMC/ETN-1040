package bo.edu.umsa.backend.specification

import bo.edu.umsa.backend.entity.Task
import bo.edu.umsa.backend.entity.TaskAssignee
import bo.edu.umsa.backend.entity.User
import jakarta.persistence.criteria.Expression
import org.springframework.data.jpa.domain.Specification
import java.sql.Timestamp
import java.util.*


class TaskSpecification {
    companion object {

        fun taskKeyword(keyword: String): Specification<Task> {
            return Specification { root, query, cb ->
                query.distinct(true)
                cb.or(
                    cb.like(cb.lower(root.get("taskName")), "%${keyword.lowercase()}%"),
                    cb.like(cb.lower(root.get("taskDescription")), "%${keyword.lowercase()}%"),
                    cb.like(cb.lower(root.get<Task>("taskAssignees").get<TaskAssignee>("user").get<User>("firstName") as Expression<String>), "%${keyword.lowercase()}%"),
                    cb.like(cb.lower(root.get<Task>("taskAssignees").get<TaskAssignee>("user").get<User>("lastName") as Expression<String>), "%${keyword.lowercase()}%"),
                    cb.like(cb.lower(root.get<Task>("taskAssignees").get<TaskAssignee>("user").get<User>("email") as Expression<String>), "%${keyword.lowercase()}%"),
                )
            }
        }

        fun taskAssignee(userId: Int): Specification<Task> {
            return Specification { root, _, cb ->
                cb.equal(root.get<Task>("taskAssignees").get<TaskAssignee>("userId"), userId)
            }
        }

        fun taskStatuses(
            taskStatuses: List<String>
        ): Specification<Task> {
            return Specification { root, query, cb ->
                query.distinct(true)
                val delayedTaskPredicate = if (taskStatuses.contains("ATRASADO"))
                    cb.and(
                        cb.notEqual(root.get<Any>("taskStatus").get<Any>("taskStatusName"), "FINALIZADO"),
                        cb.lessThan(root.get("taskDueDate"), Timestamp(System.currentTimeMillis()),
                            )
                    ) else null
                val finishedWithDelayPredicate = if (taskStatuses.contains("FINALIZADO CON RETRASO"))
                    cb.and(
                        cb.greaterThan(root.get<Date>("taskEndDate"), root.get<Date>("taskDueDate"))
                    ) else null
                val taskStatusesPredicate = cb.`in`(root.get<Any>("taskStatus").get<Any>("taskStatusName")).value(taskStatuses)
                val predicates = listOfNotNull(delayedTaskPredicate, finishedWithDelayPredicate, taskStatusesPredicate)
                cb.or(*predicates.toTypedArray())
            }
        }

        fun taskPriorities(taskPriorities: List<String>): Specification<Task> {
            return Specification { root, _, cb ->
                cb.`in`(root.get<Any>("taskPriority").get<Any>("taskPriorityName")).value(taskPriorities)
            }
        }

        fun projectId(projectId: Int): Specification<Task> {
            return Specification { root, _, cb ->
                cb.equal(root.get<Task>("project").get<Int>("projectId"), projectId)
            }
        }

        fun dateBetween(
            dateFrom: Date,
            dateTo: Date
        ): Specification<Task> {
            return Specification { root, _, cb ->
                cb.between(root.get("taskDueDate"), dateFrom, dateTo)
            }
        }

        fun dateBetweenAll(
            dateFrom: Date,
            dateTo: Date
        ): Specification<Task> {
            return Specification { root, _, cb ->
                cb.or (
                    cb.between(root.get("taskDueDate"), dateFrom, dateTo),
                    cb.between(root.get("taskEndDate"), dateFrom, dateTo),
                    cb.between(root.get("txDate"),dateFrom, dateTo),
                )
            }
        }

        fun projects(projectIds: List<Int>): Specification<Task> {
            return Specification { root, _, cb ->
                cb.`in`(root.get<Any>("projectId")).value(projectIds)
            }
        }

        fun taskAssignees(userIds: List<Int>): Specification<Task> {
            return Specification { root, _, cb ->
                cb.`in`(root.get<Any>("taskAssignees").get<Any>("userId")).value(userIds)
            }
        }

        fun statusIsTrue(): Specification<Task> {
            return Specification { root, _, cb ->
                cb.equal(root.get<Task>("status"), true)
            }
        }
    }
}