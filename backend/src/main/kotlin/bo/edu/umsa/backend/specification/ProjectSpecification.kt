package bo.edu.umsa.backend.specification

import bo.edu.umsa.backend.entity.Project
import bo.edu.umsa.backend.entity.Task
import bo.edu.umsa.backend.entity.TaskAssignee
import bo.edu.umsa.backend.entity.User
import jakarta.persistence.criteria.Expression
import org.springframework.data.jpa.domain.Specification
import java.util.*


class ProjectSpecification {
    companion object {

        fun projectKeyword(keyword: String): Specification<Project> {
            return Specification { root, query, cb ->
                query.distinct(true)
                cb.or(
                    cb.like(cb.lower(root.get("projectName")), "%${keyword.lowercase()}%"),
                    cb.like(cb.lower(root.get("projectDescription")), "%${keyword.lowercase()}%"),
                    cb.like(cb.lower(root.get<Task>("projectModerators").get<TaskAssignee>("user").get<User>("firstName") as Expression<String>), "%${keyword.lowercase()}%"),
                    cb.like(cb.lower(root.get<Task>("projectModerators").get<TaskAssignee>("user").get<User>("lastName") as Expression<String>), "%${keyword.lowercase()}%"),
                    cb.like(cb.lower(root.get<Task>("projectModerators").get<TaskAssignee>("user").get<User>("email") as Expression<String>), "%${keyword.lowercase()}%"),
                    cb.like(cb.lower(root.get<Task>("projectMembers").get<TaskAssignee>("user").get<User>("firstName") as Expression<String>), "%${keyword.lowercase()}%"),
                    cb.like(cb.lower(root.get<Task>("projectMembers").get<TaskAssignee>("user").get<User>("lastName") as Expression<String>), "%${keyword.lowercase()}%"),
                    cb.like(cb.lower(root.get<Task>("projectMembers").get<TaskAssignee>("user").get<User>("email") as Expression<String>), "%${keyword.lowercase()}%"),
                )
            }
        }

        fun dateBetweenAll(
            dateFrom: Date,
            dateTo: Date
        ): Specification<Project> {
            return Specification { root, _, cb ->
                cb.or(
                    cb.between(root.get("projectDateFrom"), dateFrom, dateTo),
                    cb.between(root.get("projectDateTo"), dateFrom, dateTo),
                    cb.between(root.get("projectEndDate"), dateFrom, dateTo),
                )
            }
        }

        fun projectOwners(userIds: List<Int>): Specification<Project> {
            return Specification { root, _, cb ->
                cb.and(cb.`in`(root.get<Any>("projectOwners").get<Any>("userId")).value(userIds), cb.equal(root.get<Any>("projectOwners").get<Any>("status"), true))
            }
        }

        fun projectModerators(userIds: List<Int>): Specification<Project> {
            return Specification { root, _, cb ->
                cb.and(cb.`in`(root.get<Any>("projectModerators").get<Any>("userId")).value(userIds), cb.equal(root.get<Any>("projectModerators").get<Any>("status"), true))
            }
        }

        fun projectMembers(userIds: List<Int>): Specification<Project> {
            return Specification { root, _, cb ->
                cb.and(cb.`in`(root.get<Any>("projectMembers").get<Any>("userId")).value(userIds), cb.equal(root.get<Any>("projectMembers").get<Any>("status"), true))
            }
        }

        fun statuses(statuses: List<String>): Specification<Project> {
            return Specification { root, query, cb ->
                query.distinct(true)
                val openProjectPredicate = if (statuses.contains("ABIERTO")) cb.and(cb.isNull(root.get<Any>("projectEndDate")), cb.greaterThanOrEqualTo(root.get<Date>("projectDateTo"), Date())) else null
                val closedProjectPredicate = if (statuses.contains("CERRADO")) cb.and(cb.isNotNull(root.get<Any>("projectEndDate")), cb.lessThan(root.get<Date>("projectEndDate"), root.get<Date>("projectDateTo"))) else null
                val delayedProjectPredicate = if (statuses.contains("ATRASADO")) cb.and(cb.isNull(root.get<Any>("projectEndDate")), cb.lessThan(root.get("projectDateTo"), Date())) else null
                val closedWithDelayProjectPredicate = if (statuses.contains("CERRADO CON RETRASO")) cb.and(cb.isNotNull(root.get<Any>("projectEndDate")), cb.greaterThanOrEqualTo(root.get<Date>("projectEndDate"), root.get<Date>("projectDateTo"))) else null
                val predicates = listOfNotNull(openProjectPredicate, closedProjectPredicate, delayedProjectPredicate, closedWithDelayProjectPredicate)
                cb.or(*predicates.toTypedArray())
            }
        }

        fun statusIsTrue(): Specification<Project> {
            return Specification { root, _, cb ->
                cb.equal(root.get<Project>("status"), true)
            }
        }
    }
}