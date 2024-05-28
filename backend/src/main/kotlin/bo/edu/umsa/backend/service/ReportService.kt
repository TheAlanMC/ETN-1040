package bo.edu.umsa.backend.service

import bo.edu.umsa.backend.dto.*
import bo.edu.umsa.backend.entity.Project
import bo.edu.umsa.backend.entity.Report
import bo.edu.umsa.backend.entity.Task
import bo.edu.umsa.backend.exception.EtnException
import bo.edu.umsa.backend.mapper.ReportMapper
import bo.edu.umsa.backend.repository.*
import bo.edu.umsa.backend.specification.ProjectSpecification
import bo.edu.umsa.backend.specification.ReportSpecification
import bo.edu.umsa.backend.specification.TaskSpecification
import bo.edu.umsa.backend.util.AuthUtil
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.Instant

@Service
class ReportService @Autowired constructor(
    private val fileRepository: FileRepository,
    private val reportRepository: ReportRepository,
    private val projectRepository: ProjectRepository,
    private val taskRepository: TaskRepository,
    private val taskStatusRepository: TaskStatusRepository,
    private val taskPriorityRepository: TaskPriorityRepository
) {
    companion object {
        private val logger = LoggerFactory.getLogger(ReportService::class.java.name)
    }

    fun getReports(
        sortBy: String,
        sortType: String,
        page: Int,
        size: Int,
        dateFrom: String,
        dateTo: String,
    ): Page<ReportDto> {
        try {
            Timestamp.from(Instant.parse(dateFrom))
            Timestamp.from(Instant.parse(dateTo))
        } catch (e: Exception) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Date format is incorrect", "El formato de fecha es incorrecto")
        }
        if (Timestamp.from(Instant.parse(dateFrom)).after(Timestamp.from(Instant.parse(dateTo)))) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Date range is incorrect", "El rango de fechas es incorrecto")
        }
        // Pagination and sorting
        val pageable: Pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortType), sortBy))
        var specification: Specification<Report> = Specification.where(null)
        specification = specification.and(ReportSpecification.statusIsTrue())

        specification = specification.and(ReportSpecification.dateBetween(Timestamp.from(Instant.parse(dateFrom)), Timestamp.from(Instant.parse(dateTo).plusSeconds(60 * 60 * 24 - 1))))

        val reportEntities: Page<Report> = reportRepository.findAll(specification, pageable)
        logger.info("Success: Reports retrieved")
        return reportEntities.map { ReportMapper.entityToDto(it) }
    }


    fun uploadReportFile(
        file: FilePartialDto,
        reportType: ReportType,
        dateFrom: String,
        dateTo: String
    ) {
        if (file.fileName.isBlank()) throw EtnException(HttpStatus.BAD_REQUEST, "Error: Empty fields", "Al menos un campo está vacío")
        try {
            Timestamp.from(Instant.parse(dateFrom))
            Timestamp.from(Instant.parse(dateTo))
        } catch (e: Exception) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Date format is incorrect", "El formato de fecha es incorrecto")
        }
        if (Timestamp.from(Instant.parse(dateFrom)).after(Timestamp.from(Instant.parse(dateTo)))) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Date range is incorrect", "El rango de fechas es incorrecto")
        }
        // Get the user id from the token
        val userId = AuthUtil.getUserIdFromAuthToken() ?: throw EtnException(HttpStatus.UNAUTHORIZED, "Error: Unauthorized", "No autorizado")
        // Validate the file exists
        val fileEntity = fileRepository.findByFileIdAndStatusIsTrue(file.fileId.toLong())
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: File not found", "Archivo no encontrado")
        logger.info("Starting the service call to upload the report file")

        val fileName = file.fileName.split(".")
        val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy")
        val simpleDateFrom = simpleDateFormat.format(Timestamp.from(Instant.parse(dateFrom)))
        val simpleDateTo = simpleDateFormat.format(Timestamp.from(Instant.parse(dateTo)))
        val reportEntity = Report()
        reportEntity.userId = userId.toInt()
        reportEntity.fileId = file.fileId
        reportEntity.reportType = reportType
        reportEntity.reportStartDate = Timestamp.from(Instant.parse(dateFrom))
        reportEntity.reportEndDate = Timestamp.from(Instant.parse(dateTo))
        reportEntity.reportName = "${fileName[0]}: $simpleDateFrom - $simpleDateTo.${fileName[1]}"
        reportRepository.save(reportEntity)
        logger.info("Success: Report file uploaded")
    }

    fun getTaskFilters(
        dateFrom: String,
        dateTo: String
    ): TaskReportFiltersDto {
        logger.info("Starting the service call to get the task filters")
        try {
            Timestamp.from(Instant.parse(dateFrom))
            Timestamp.from(Instant.parse(dateTo))
        } catch (e: Exception) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Date format is incorrect", "El formato de fecha es incorrecto")
        }
        if (Timestamp.from(Instant.parse(dateFrom)).after(Timestamp.from(Instant.parse(dateTo)))) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Date range is incorrect", "El rango de fechas es incorrecto")
        }

        val taskEntities = taskRepository.findAllTasksByDateRange(Timestamp.from(Instant.parse(dateFrom)), Timestamp.from(Instant.parse(dateTo).plusSeconds(60 * 60 * 24 - 1))).distinctBy { it.taskId }
        val projectsEntity = taskEntities.map { it.project }.distinct().filterNotNull()
        val statusesEntity = taskStatusRepository.findAllByStatusIsTrueOrderByTaskStatusId()

        val prioritiesEntity = taskPriorityRepository.findAllByStatusIsTrueOrderByTaskPriorityId()
        val taskAssigneeEntities = taskEntities.flatMap { task -> task.taskAssignees?.filter { it.status } ?: emptyList() }.distinctBy { it.userId }

        val taskReportFiltersDto = TaskReportFiltersDto(projects = projectsEntity.map { project ->
            TaskReportProjectDto(
                projectId = project.projectId,
                projectName = project.projectName,
            )
        }, statuses = statusesEntity.map { status ->
            TaskReportStatusDto(
                statusId = status.taskStatusId,
                statusName = status.taskStatusName,
            )
        } + TaskReportStatusDto(
            statusId = 4,
            statusName = "ATRASADO",
        ) + TaskReportStatusDto(
            statusId = 5,
            statusName = "FINALIZADO CON RETRASO",
        ), priorities = prioritiesEntity.map { priority ->
            TaskReportPriorityDto(
                priorityId = priority.taskPriorityId,
                priorityName = priority.taskPriorityName,
            )
        }, taskAssignees = taskAssigneeEntities.map { taskAssignee ->
            TaskReportAssigneeDto(
                userId = taskAssignee.userId,
                firstName = taskAssignee.user!!.firstName,
                lastName = taskAssignee.user!!.lastName,
                email = taskAssignee.user!!.email,
            )
        })
        logger.info("Success: Task filters retrieved")
        return taskReportFiltersDto
    }

    fun getProjectTasks(
        sortBy: String,
        sortType: String,
        page: Int,
        size: Int,
        dateFrom: String,
        dateTo: String,
        projects: List<Int>?,
        taskAssignees: List<Int>?,
        statuses: List<String>?,
        priorities: List<String>?,
    ): Page<TaskReportDto> {
        logger.info("Starting the service call to get the tasks")
        try {
            Timestamp.from(Instant.parse(dateFrom))
            Timestamp.from(Instant.parse(dateTo))
        } catch (e: Exception) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Date format is incorrect", "El formato de fecha es incorrecto")
        }
        if (Timestamp.from(Instant.parse(dateFrom)).after(Timestamp.from(Instant.parse(dateTo)))) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Date range is incorrect", "El rango de fechas es incorrecto")
        }

        // Pagination and sorting
        val pageable: Pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortType), sortBy))
        var specification: Specification<Task> = Specification.where(null)
        specification = specification.and(TaskSpecification.statusIsTrue())
        specification = specification.and(TaskSpecification.dateBetweenAll(Timestamp.from(Instant.parse(dateFrom)), Timestamp.from(Instant.parse(dateTo).plusSeconds(60 * 60 * 24 - 1))))

        if (!statuses.isNullOrEmpty()) {
            specification = specification.and(TaskSpecification.taskStatuses(statuses))
        }

        if (!priorities.isNullOrEmpty()) {
            specification = specification.and(TaskSpecification.taskPriorities(priorities))
        }

        if (!projects.isNullOrEmpty()) {
            specification = specification.and(TaskSpecification.projects(projects))
        }

        if (!taskAssignees.isNullOrEmpty()) {
            specification = specification.and(TaskSpecification.taskAssignees(taskAssignees))
        }

        val taskEntities: Page<Task> = taskRepository.findAll(specification, pageable)
        logger.info("Success: Tasks retrieved")
        return taskEntities.map {
            TaskReportDto(
                taskId = it.taskId,
                taskName = it.taskName,
                projectName = it.project!!.projectName,
                taskStatusName = if (it.taskEndDate != null && it.taskEndDate!!.after(it.taskDueDate)) "FINALIZADO CON RETRASO" else if (it.taskEndDate == null && it.taskDueDate.before(Timestamp.from(Instant.now()))) "ATRASADO" else it.taskStatus!!.taskStatusName,
                taskPriorityName = it.taskPriority!!.taskPriorityName,
                taskCreationDate = it.txDate,
                taskDueDate = it.taskDueDate,
                taskEndDate = it.taskEndDate,
                taskRating = it.taskRating,
                replacedPartDescription = it.replacedParts?.joinToString { replacedPart -> replacedPart.replacedPartDescription } ?: "",
                taskAssignees = it.taskAssignees!!.map { taskAssignee ->
                    taskAssignee.user!!.firstName + " " + taskAssignee.user!!.lastName
                },
            )
        }
    }

    fun getProjectFilters(
        dateFrom: String,
        dateTo: String
    ): ProjectReportFiltersDto {
        logger.info("Starting the service call to get the project filters")
        try {
            Timestamp.from(Instant.parse(dateFrom))
            Timestamp.from(Instant.parse(dateTo))
        } catch (e: Exception) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Date format is incorrect", "El formato de fecha es incorrecto")
        }
        if (Timestamp.from(Instant.parse(dateFrom)).after(Timestamp.from(Instant.parse(dateTo)))) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Date range is incorrect", "El rango de fechas es incorrecto")
        }

        val projectEntities = projectRepository.findAllProjectsByDateRange(Timestamp.from(Instant.parse(dateFrom)), Timestamp.from(Instant.parse(dateTo).plusSeconds(60 * 60 * 24 - 1))).distinctBy { it.projectId }
        val projectOwnerEntities = projectEntities.flatMap { project ->
            project.projectOwners?.filter { it.status } ?: emptyList()
        }.distinctBy { it.userId }
        val projectModeratorEntities = projectEntities.flatMap { project ->
            project.projectModerators?.filter { it.status } ?: emptyList()
        }.distinctBy { it.userId }
        val projectMemberEntities = projectEntities.flatMap { project ->
            project.projectMembers?.filter { it.status } ?: emptyList()
        }.distinctBy { it.userId }

        val customStatuses = listOf(ProjectReportStatusDto(
            statusId = 1,
            statusName = "ABIERTO",
        ), ProjectReportStatusDto(
            statusId = 2,
            statusName = "CERRADO",
        ), ProjectReportStatusDto(
            statusId = 3,
            statusName = "ATRASADO",
        ), ProjectReportStatusDto(
            statusId = 4,
            statusName = "CERRADO CON RETRASO",
        ))
        val projectReportFiltersDto = ProjectReportFiltersDto(statuses = customStatuses, projectOwners = projectOwnerEntities.map { projectOwner ->
            ProjectReportOwnerDto(
                userId = projectOwner.userId,
                firstName = projectOwner.user!!.firstName,
                lastName = projectOwner.user!!.lastName,
                email = projectOwner.user!!.email,
            )
        }, projectModerators = projectModeratorEntities.map { projectModerator ->
            ProjectReportModeratorDto(
                userId = projectModerator.userId,
                firstName = projectModerator.user!!.firstName,
                lastName = projectModerator.user!!.lastName,
                email = projectModerator.user!!.email,
            )
        }, projectMembers = projectMemberEntities.map { projectMember ->
            ProjectReportMemberDto(
                userId = projectMember.userId,
                firstName = projectMember.user!!.firstName,
                lastName = projectMember.user!!.lastName,
                email = projectMember.user!!.email,
            )
        })
        logger.info("Success: Project filters retrieved")
        return projectReportFiltersDto
    }

    fun getProjects(
        sortBy: String,
        sortType: String,
        page: Int,
        size: Int,
        dateFrom: String,
        dateTo: String,
        projectOwners: List<Int>?,
        projectModerators: List<Int>?,
        projectMembers: List<Int>?,
        statuses: List<String>?,
    ): Page<ProjectReportDto> {
        logger.info("Starting the service call to get the projects")
        try {
            Timestamp.from(Instant.parse(dateFrom))
            Timestamp.from(Instant.parse(dateTo))
        } catch (e: Exception) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Date format is incorrect", "El formato de fecha es incorrecto")
        }
        if (Timestamp.from(Instant.parse(dateFrom)).after(Timestamp.from(Instant.parse(dateTo)))) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Date range is incorrect", "El rango de fechas es incorrecto")
        }

        // Pagination and sorting
        val pageable: Pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortType), sortBy))
        var specification: Specification<Project> = Specification.where(null)
        specification = specification.and(ProjectSpecification.statusIsTrue())
        specification = specification.and(ProjectSpecification.dateBetweenAll(Timestamp.from(Instant.parse(dateFrom)), Timestamp.from(Instant.parse(dateTo).plusSeconds(60 * 60 * 24 - 1))))

        if (!statuses.isNullOrEmpty()) {
            specification = specification.and(ProjectSpecification.statuses(statuses))
        }

        if (!projectOwners.isNullOrEmpty()) {
            specification = specification.and(ProjectSpecification.projectOwners(projectOwners))
        }

        if (!projectModerators.isNullOrEmpty()) {
            specification = specification.and(ProjectSpecification.projectModerators(projectModerators))
        }

        if (!projectMembers.isNullOrEmpty()) {
            specification = specification.and(ProjectSpecification.projectMembers(projectMembers))
        }

        val projectEntities: Page<Project> = projectRepository.findAll(specification, pageable)

        logger.info("Success: Projects retrieved")
        return projectEntities.map {
            ProjectReportDto(
                taskReports = emptyList(),
                projectId = it.projectId,
                projectName = it.projectName,
                projectStatusName = if (it.projectEndDate != null && it.projectEndDate!!.after(it.projectDateTo)) "CERRADO CON RETRASO" else if (it.projectEndDate != null) "CERRADO" else if (it.projectDateTo.before(Timestamp.from(Instant.now()))) "ATRASADO" else "ABIERTO",
                projectFinishedTasks = it.tasks!!.count { task -> task.taskEndDate != null },
                projectTotalTasks = it.tasks!!.size,
                projectDateFrom = it.projectDateFrom,
                projectDateTo = it.projectDateTo,
                projectEndDate = it.projectEndDate,
                projectOwners = it.projectOwners!!.map { projectOwner ->
                    projectOwner.user!!.firstName + " " + projectOwner.user!!.lastName
                },
                projectModerators = it.projectModerators!!.map { projectModerator ->
                    projectModerator.user!!.firstName + " " + projectModerator.user!!.lastName
                },
                projectMembers = it.projectMembers!!.map { projectMember ->
                    projectMember.user!!.firstName + " " + projectMember.user!!.lastName
                },
            )
        }
    }

    fun getExecutiveReport(
        dateFrom: String,
        dateTo: String
    ): ExecutiveReportDto {
        logger.info("Starting the service call to get the executive report")
        try {
            Timestamp.from(Instant.parse(dateFrom))
            Timestamp.from(Instant.parse(dateTo))
        } catch (e: Exception) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Date format is incorrect", "El formato de fecha es incorrecto")
        }
        if (Timestamp.from(Instant.parse(dateFrom)).after(Timestamp.from(Instant.parse(dateTo)))) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Date range is incorrect", "El rango de fechas es incorrecto")
        }

        val projectEntities: List<Project> = projectRepository.findAllProjectsByDateRange(Timestamp.from(Instant.parse(dateFrom)), Timestamp.from(Instant.parse(dateTo).plusSeconds(60 * 60 * 24 - 1))).distinctBy { it.projectId }
        projectEntities.forEach { project ->
            project.tasks = project.tasks?.filter { it.status }?.distinctBy { it.taskId }
        }

        return ExecutiveReportDto(
            totalProjects = projectEntities.size,
            completedProjects = projectEntities.count { it.projectEndDate != null && it.projectDateTo.after(it.projectEndDate) },
            completedWithDelayProjects = projectEntities.count { it.projectEndDate != null && it.projectEndDate!!.after(it.projectDateTo) },
            inProgressProjects = projectEntities.count { it.projectEndDate == null && it.projectDateTo.after(Timestamp.from(Instant.now())) },
            delayedProjects = projectEntities.count { it.projectEndDate == null && it.projectDateTo.before(Timestamp.from(Instant.now())) },
            totalTasks = projectEntities.sumOf { it.tasks!!.size },
            completedTasks = projectEntities.sumOf { it.tasks!!.count { task -> task.taskEndDate != null && task.taskDueDate.after(task.taskEndDate) } },
            completedWithDelayTasks = projectEntities.sumOf { it.tasks!!.count { task -> task.taskEndDate != null && task.taskEndDate!!.after(task.taskDueDate) } },
            inProgressTasks = projectEntities.sumOf { it.tasks!!.count { task -> task.taskEndDate == null && task.taskDueDate.after(Timestamp.from(Instant.now())) && task.taskStatus!!.taskStatusName == "EN PROGRESO" } },
            pendingTasks = projectEntities.sumOf { it.tasks!!.count { task -> task.taskEndDate == null && task.taskDueDate.after(Timestamp.from(Instant.now())) && task.taskStatus!!.taskStatusName == "PENDIENTE" } },
            delayedTasks = projectEntities.sumOf { it.tasks!!.count { task -> task.taskEndDate == null && task.taskDueDate.before(Timestamp.from(Instant.now())) } },
            highPriorityTasks = projectEntities.sumOf { it.tasks!!.count { task -> task.taskPriority!!.taskPriorityName == "ALTA" } },
            mediumPriorityTasks = projectEntities.sumOf { it.tasks!!.count { task -> task.taskPriority!!.taskPriorityName == "MEDIA" } },
            lowPriorityTasks = projectEntities.sumOf { it.tasks!!.count { task -> task.taskPriority!!.taskPriorityName == "BAJA" } },
            projectReports = projectEntities.map {
                ProjectReportDto(
                    projectId = it.projectId,
                    projectName = it.projectName,
                    projectStatusName = if (it.projectEndDate != null && it.projectEndDate!!.after(it.projectDateTo)) "CERRADO CON RETRASO" else if (it.projectEndDate != null) "CERRADO" else if (it.projectDateTo.before(Timestamp.from(Instant.now()))) "ATRASADO" else "ABIERTO",
                    projectFinishedTasks = it.tasks!!.count { task -> task.taskEndDate != null },
                    projectTotalTasks = it.tasks!!.size,
                    projectDateFrom = it.projectDateFrom,
                    projectDateTo = it.projectDateTo,
                    projectEndDate = it.projectEndDate,
                    projectOwners = it.projectOwners!!.map { projectOwner ->
                        projectOwner.user!!.firstName + " " + projectOwner.user!!.lastName
                    },
                    projectModerators = it.projectModerators!!.map { projectModerator ->
                        projectModerator.user!!.firstName + " " + projectModerator.user!!.lastName
                    },
                    projectMembers = it.projectMembers!!.map { projectMember ->
                        projectMember.user!!.firstName + " " + projectMember.user!!.lastName
                    },
                    taskReports = it.tasks!!.map { task ->
                        TaskReportDto(
                            taskId = task.taskId,
                            taskName = task.taskName,
                            projectName = it.projectName,
                            taskStatusName = if (task.taskEndDate != null && task.taskEndDate!!.after(task.taskDueDate)) "FINALIZADO CON RETRASO" else if (task.taskEndDate == null && task.taskDueDate.before(Timestamp.from(Instant.now()))) "ATRASADO" else task.taskStatus!!.taskStatusName,
                            taskPriorityName = task.taskPriority!!.taskPriorityName,
                            taskCreationDate = task.txDate,
                            taskDueDate = task.taskDueDate,
                            taskEndDate = task.taskEndDate,
                            taskRating = task.taskRating,
                            replacedPartDescription = task.replacedParts?.joinToString { replacedPart -> replacedPart.replacedPartDescription } ?: "",
                            taskAssignees = task.taskAssignees!!.map { taskAssignee ->
                                taskAssignee.user!!.firstName + " " + taskAssignee.user!!.lastName
                            },
                        )
                    },
                )
            },
        )
    }
}