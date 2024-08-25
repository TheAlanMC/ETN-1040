package bo.edu.umsa.backend.service

import bo.edu.umsa.backend.dto.AssistantDto
import bo.edu.umsa.backend.dto.NewSemesterDto
import bo.edu.umsa.backend.dto.SemesterDto
import bo.edu.umsa.backend.entity.Assistant
import bo.edu.umsa.backend.entity.Semester
import bo.edu.umsa.backend.exception.EtnException
import bo.edu.umsa.backend.mapper.SemesterMapper
import bo.edu.umsa.backend.mapper.UserPartialMapper
import bo.edu.umsa.backend.repository.AssistantRepository
import bo.edu.umsa.backend.repository.SemesterRepository
import bo.edu.umsa.backend.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class SemesterService @Autowired constructor(
    private val semesterRepository: SemesterRepository,
    private val assistantRepository: AssistantRepository,
    private val userRepository: UserRepository,
    ) {
    companion object {
        private val logger = LoggerFactory.getLogger(SemesterService::class.java)
    }

    fun getAllSemesters(): List<SemesterDto> {
        logger.info("Getting all semesters")
        val semesterEntities = semesterRepository.findAllByStatusIsTrueOrderBySemesterIdDesc()
        return semesterEntities.map { SemesterMapper.entityToDto(it) }
    }


    fun getSemesterById(semesterId: Long): SemesterDto {
        logger.info("Getting the semester with id $semesterId")
        // Validate the semester exists
        val semesterEntity = semesterRepository.findBySemesterIdAndStatusIsTrue(semesterId)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: Semester not found", "Semestre no encontrado")
        return SemesterMapper.entityToDto(semesterEntity)
    }

    fun createSemester(newSemesterDto: NewSemesterDto) {
        // Validate the semester name is not empty
        if (newSemesterDto.semesterName.trim().isEmpty()) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Semester name is blank", "El nombre del semestre está en blanco")
        }
        // Validate the semester date from is not empty
        if (newSemesterDto.semesterDateFrom.trim().isEmpty()) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Semester date from is blank", "La fecha de inicio del semestre está en blanco")
        }
        // Validate the semester date to is not empty
        if (newSemesterDto.semesterDateTo.trim().isEmpty()) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Semester date to is blank", "La fecha de fin del semestre está en blanco")
        }
        // Validate the semester date from is before the semester date to
        val dateFrom: LocalDate = LocalDate.parse(newSemesterDto.semesterDateFrom)
        val dateTo: LocalDate = LocalDate.parse(newSemesterDto.semesterDateTo)
        if (dateFrom.isAfter(dateTo)) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Semester date from is after the semester date to", "La fecha de inicio del semestre es después de la fecha de fin del semestre")
        }

        // Create the semester
        val semesterEntity = Semester()
        semesterEntity.semesterName = newSemesterDto.semesterName
        semesterEntity.semesterDateFrom = dateFrom
        semesterEntity.semesterDateTo = dateTo
        semesterRepository.save(semesterEntity)
        logger.info("Semester created with id ${semesterEntity.semesterId}")
    }

    fun updateSemester(
        semesterId: Long,
        newSemesterDto: NewSemesterDto
    ) {
        // Validate the semester name is not empty
        if (newSemesterDto.semesterName.trim().isEmpty()) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Semester name is blank", "El nombre del semestre está en blanco")
        }
        // Validate the semester date from is not empty
        if (newSemesterDto.semesterDateFrom.trim().isEmpty()) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Semester date from is blank", "La fecha de inicio del semestre está en blanco")
        }
        // Validate the semester date to is not empty
        if (newSemesterDto.semesterDateTo.trim().isEmpty()) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Semester date to is blank", "La fecha de fin del semestre está en blanco")
        }
        // Validate the semester date from is before the semester date to
        val dateFrom: LocalDate = LocalDate.parse(newSemesterDto.semesterDateFrom)
        val dateTo: LocalDate = LocalDate.parse(newSemesterDto.semesterDateTo)
        if (dateFrom.isAfter(dateTo)) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Semester date from is after the semester date to", "La fecha de inicio del semestre es después de la fecha de fin del semestre")
        }
        // Validate the semester exists
        val semesterEntity = semesterRepository.findBySemesterIdAndStatusIsTrue(semesterId)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: Semester not found", "Semestre no encontrado")
        // Update the semester
        semesterEntity.semesterName = newSemesterDto.semesterName
        semesterEntity.semesterDateFrom = dateFrom
        semesterEntity.semesterDateTo = dateTo
        semesterRepository.save(semesterEntity)
        logger.info("Semester updated with id $semesterId")
    }

    fun deleteSemester(semesterId: Long) {
        // Validate the semester exists
        val semesterEntity = semesterRepository.findBySemesterIdAndStatusIsTrue(semesterId)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: Semester not found", "Semestre no encontrado")
        // Validate the semester has no assistants assigned
        val assistantEntities = assistantRepository.findAllBySemesterIdAndStatusIsTrue(semesterId)
        if (assistantEntities.isNotEmpty()) throw EtnException(HttpStatus.BAD_REQUEST, "Error: Semester has assistants assigned", "El semestre tiene auxiliares asignados")
        // Delete the semester
        semesterEntity.status = false
        semesterRepository.save(semesterEntity)
    }

    fun getAssistantsBySemesterId(semesterId: Long): List<AssistantDto> {
        logger.info("Getting assistants for semester with id $semesterId")
        // Validate the semester exists
        semesterRepository.findBySemesterIdAndStatusIsTrue(semesterId)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: Semester not found", "Semestre no encontrado")
        // Get the assistants
        val assistantEntities = assistantRepository.findAllBySemesterIdAndStatusIsTrue(semesterId)
        return assistantEntities.map {
            AssistantDto(
                it.assistantId,
                UserPartialMapper.entityToDto(it.user!!))
        }
    }

    fun addAssistantsToSemester(
        semesterId: Long,
        assistantIds: List<Long>
    ) {
        // Validate that there are exactly 6 assistants, with unique ids
        if (assistantIds.size != 6) throw EtnException(HttpStatus.BAD_REQUEST, "Error: Exactly 6 assistants are required", "Se requieren exactamente 6 auxiliares")
        if (assistantIds.distinct().size != 6) throw EtnException(HttpStatus.BAD_REQUEST, "Error: Duplicate assistants are not allowed", "No se permiten auxiliares duplicados")

        // Validate the semester exists
        semesterRepository.findBySemesterIdAndStatusIsTrue(semesterId)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: Semester not found", "Semestre no encontrado")
        // Validate the semester has no assistants assigned
        val assistantEntities = assistantRepository.findAllBySemesterIdAndStatusIsTrue(semesterId)
        if (assistantEntities.isNotEmpty()) throw EtnException(HttpStatus.BAD_REQUEST, "Error: Semester has assistants assigned", "El semestre tiene auxiliares asignados")
        // Validate the users exists
        val userEntities = userRepository.findAllByUserIdInAndStatusIsTrue(assistantIds.map { it.toInt() })
        if (userEntities.size != assistantIds.size) throw EtnException(HttpStatus.NOT_FOUND, "Error: User not found", "Al menos un usuario no fue encontrado")
        // Delete previous assistants changing the status to false
        logger.info("Deleting previous assistants")
        assistantEntities.forEach {
            it.status = false
            assistantRepository.save(it)
        }
        // Add the assistants to the semester
        logger.info("Adding new assistants")
        assistantIds.forEach {
            val assistantEntity = Assistant()
            assistantEntity.userId = it.toInt()
            assistantEntity.semesterId = semesterId.toInt()
            assistantRepository.save(assistantEntity)
        }
    }
}






