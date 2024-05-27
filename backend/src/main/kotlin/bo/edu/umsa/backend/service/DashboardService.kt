package bo.edu.umsa.backend.service

import bo.edu.umsa.backend.dto.ProjectByDateDto
import bo.edu.umsa.backend.dto.ProjectDashboardDto
import bo.edu.umsa.backend.dto.TaskByDateDto
import bo.edu.umsa.backend.dto.TaskDashboardDto
import bo.edu.umsa.backend.exception.EtnException
import bo.edu.umsa.backend.repository.ProjectRepository
import bo.edu.umsa.backend.repository.TaskRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.time.Instant

@Service
class DashboardService @Autowired constructor(
    private val projectRepository: ProjectRepository,
    private val taskRepository: TaskRepository
) {
    companion object {
        private val logger = LoggerFactory.getLogger(DashboardService::class.java.name)
    }

    fun getTaskDashboard(
        dateFrom: String,
        dateTo: String
    ): TaskDashboardDto {
        try {
            Timestamp.from(Instant.parse(dateFrom))
            Timestamp.from(Instant.parse(dateTo))
        } catch (e: Exception) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Date format is incorrect", "El formato de fecha es incorrecto")
        }
        if (Timestamp.from(Instant.parse(dateFrom)).after(Timestamp.from(Instant.parse(dateTo)))) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Date range is incorrect", "El rango de fechas es incorrecto")
        }
        logger.info("Getting the task dashboard")
        val taskEntities = taskRepository.findAllTasksByDateRange(Timestamp.from(Instant.parse(dateFrom)), Timestamp.from(Instant.parse(dateTo).plusSeconds(60 * 60 * 24 - 1))).distinctBy { it.taskId }

        val completedTaskEntities = taskEntities.filter { task -> task.taskEndDate != null && task.taskDueDate.after(task.taskEndDate) }
        val completedWithDelayTaskEntities = taskEntities.filter { task -> task.taskEndDate != null && task.taskEndDate!!.after(task.taskDueDate) }
        val inProgressTaskEntities = taskEntities.filter { task -> task.taskEndDate == null && task.taskDueDate.after(Timestamp.from(Instant.now())) && task.taskStatus!!.taskStatusName == "EN PROGRESO" }
        val pendingTaskEntities = taskEntities.filter { task -> task.taskEndDate == null && task.taskDueDate.after(Timestamp.from(Instant.now())) && task.taskStatus!!.taskStatusName == "PENDIENTE" }
        val delayedTaskEntities = taskEntities.filter { task -> task.taskEndDate == null && task.taskDueDate.before(Timestamp.from(Instant.now())) }
        val highPriorityTaskEntities = taskEntities.filter { task -> task.taskPriority!!.taskPriorityName == "ALTA" }
        val mediumPriorityTaskEntities = taskEntities.filter { task -> task.taskPriority!!.taskPriorityName == "MEDIA" }
        val lowPriorityTaskEntities = taskEntities.filter { task -> task.taskPriority!!.taskPriorityName == "BAJA" }

        val taskByDate = taskEntities.groupBy { Pair(it.txDate.toLocalDateTime().year, it.txDate.toLocalDateTime().monthValue) }.map { (yearMonth, tasks) ->
            TaskByDateDto(
                year = yearMonth.first,
                month = yearMonth.second,
                completedTasks = tasks.count { it.taskEndDate != null && it.taskDueDate.after(it.taskEndDate) },
                completedWithDelayTasks = tasks.count { it.taskEndDate != null && it.taskEndDate!!.after(it.taskDueDate) },
                inProgressTasks = tasks.count { it.taskEndDate == null && it.taskDueDate.after(Timestamp.from(Instant.now())) && it.taskStatus!!.taskStatusName == "EN PROGRESO" },
                pendingTasks = tasks.count { it.taskEndDate == null && it.taskDueDate.after(Timestamp.from(Instant.now())) && it.taskStatus!!.taskStatusName == "PENDIENTE" },
                delayedTasks = tasks.count { it.taskEndDate == null && it.taskDueDate.before(Timestamp.from(Instant.now())) },
            )
        }

        logger.info("Task dashboard obtained")

        return TaskDashboardDto(
            totalTasks = taskEntities.size,
            totalCompletedTasks = completedTaskEntities.size,
            totalCompletedWithDelayTasks = completedWithDelayTaskEntities.size,
            totalInProgressTasks = inProgressTaskEntities.size,
            totalPendingTasks = pendingTaskEntities.size,
            totalDelayedTasks = delayedTaskEntities.size,
            totalHighPriorityTasks = highPriorityTaskEntities.size,
            totalMediumPriorityTasks = mediumPriorityTaskEntities.size,
            totalLowPriorityTasks = lowPriorityTaskEntities.size,
            taskByDate = taskByDate,
        )
    }

    fun getProjectDashboard(
        dateFrom: String,
        dateTo: String
    ): ProjectDashboardDto {
        try {
            Timestamp.from(Instant.parse(dateFrom))
            Timestamp.from(Instant.parse(dateTo))
        } catch (e: Exception) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Date format is incorrect", "El formato de fecha es incorrecto")
        }
        if (Timestamp.from(Instant.parse(dateFrom)).after(Timestamp.from(Instant.parse(dateTo)))) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Date range is incorrect", "El rango de fechas es incorrecto")
        }
        logger.info("Getting the project dashboard")
        val projectEntities = projectRepository.findAllProjectsByDateRange(Timestamp.from(Instant.parse(dateFrom)), Timestamp.from(Instant.parse(dateTo).plusSeconds(60 * 60 * 24 - 1))).distinctBy { it.projectId }

        val completedProjectEntities = projectEntities.filter { project -> project.projectEndDate != null && project.projectDateTo.after(project.projectEndDate) }
        val completedWithDelayProjectEntities = projectEntities.filter { project -> project.projectEndDate != null && project.projectEndDate!!.after(project.projectDateTo) }
        val inProgressProjectEntities = projectEntities.filter { project -> project.projectEndDate == null && project.projectDateTo.after(Timestamp.from(Instant.now())) }
        val delayedProjectEntities = projectEntities.filter { project -> project.projectEndDate == null && project.projectDateTo.before(Timestamp.from(Instant.now())) }

        val projectByDate = projectEntities.groupBy { Pair(it.txDate.toLocalDateTime().year, it.txDate.toLocalDateTime().monthValue) }.map { (yearMonth, projects) ->
            ProjectByDateDto(
                year = yearMonth.first,
                month = yearMonth.second,
                completedProjects = projects.count { it.projectEndDate != null && it.projectDateTo.after(it.projectEndDate) },
                completedWithDelayProjects = projects.count { it.projectEndDate != null && it.projectEndDate!!.after(it.projectDateTo) },
                inProgressProjects = projects.count { it.projectEndDate == null && it.projectDateTo.after(Timestamp.from(Instant.now())) },
                delayedProjects = projects.count { it.projectEndDate == null && it.projectDateTo.before(Timestamp.from(Instant.now())) },
            )
        }

        logger.info("Project dashboard obtained")

        return ProjectDashboardDto(
            totalProjects = projectEntities.size,
            totalCompletedProjects = completedProjectEntities.size,
            totalCompletedWithDelayProjects = completedWithDelayProjectEntities.size,
            totalInProgressProjects = inProgressProjectEntities.size,
            totalDelayedProjects = delayedProjectEntities.size,
            projectByDate = projectByDate,
        )
    }
}