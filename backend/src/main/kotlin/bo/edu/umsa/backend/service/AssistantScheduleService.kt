package bo.edu.umsa.backend.service

import bo.edu.umsa.backend.dto.AssistantScheduleDto
import bo.edu.umsa.backend.dto.NewAssistantScheduleDto
import bo.edu.umsa.backend.entity.AssistantSchedule
import bo.edu.umsa.backend.exception.EtnException
import bo.edu.umsa.backend.mapper.ScheduleMapper
import bo.edu.umsa.backend.mapper.UserPartialMapper
import bo.edu.umsa.backend.repository.AssistantRepository
import bo.edu.umsa.backend.repository.AssistantScheduleRepository
import bo.edu.umsa.backend.repository.SemesterRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import kotlin.math.abs

@Service
class AssistantScheduleService @Autowired constructor(
    private val semesterRepository: SemesterRepository,
    private val assistantRepository: AssistantRepository,
    private val assistantScheduleRepository: AssistantScheduleRepository,
) {

    companion object {
        private val logger = LoggerFactory.getLogger(AssistantScheduleService::class.java)
    }

    fun getAssistantScheduleBySemesterId(semesterId: Long): List<AssistantScheduleDto> {
        logger.info("Getting assistant schedules for semester with id $semesterId")
        // Validate the semester exists
        semesterRepository.findBySemesterIdAndStatusIsTrue(semesterId)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: Semester not found", "Semestre no encontrado")
        // Get the assistant schedules
        val assistantScheduleEntities = assistantScheduleRepository.findAllBySemesterIdAndStatusIsTrue(semesterId)

        return assistantScheduleEntities.groupBy { it.assistantId }.map { (assistantId, assistantSchedules) ->
            val assistantEntity = assistantRepository.findByAssistantIdAndStatusIsTrue(assistantId.toLong())
                ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: Assistant not found", "Auxiliar no encontrado")
            val schedules = assistantSchedules.map { ScheduleMapper.entityToDto(it.schedule!!) }.sortedBy { it.scheduleId }
            AssistantScheduleDto(assistantEntity.assistantId, UserPartialMapper.entityToDto(assistantEntity.user!!), schedules)
        }
    }

    fun getAssistantScheduleByAssistantId(assistantId: Long): AssistantScheduleDto {
        logger.info("Getting assistant schedules for assistant with id $assistantId")
        // Validate the assistant exists
        assistantRepository.findByAssistantIdAndStatusIsTrue(assistantId)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: Assistant not found", "Auxiliar no encontrado")
        // Get the assistant schedules
        val assistantScheduleEntities = assistantScheduleRepository.findAllByAssistantIdAndStatusIsTrue(assistantId)
        if (assistantScheduleEntities.isEmpty()) {
            throw EtnException(HttpStatus.NOT_FOUND, "Error: Assistant schedule not found", "Horario de auxiliar no encontrado")
        }
        return AssistantScheduleDto(assistantScheduleEntities.first().assistantId, UserPartialMapper.entityToDto(assistantScheduleEntities.first().assistant!!.user!!), assistantScheduleEntities.map { ScheduleMapper.entityToDto(it.schedule!!) }.sortedBy { it.scheduleId })
    }

    fun addCustomAssistantsScheduleToSemester(
        semesterId: Long,
        newAssistantScheduleDto: List<NewAssistantScheduleDto>
    ) {
        // Validate the schedules are not repeated
        if (newAssistantScheduleDto.map { it.scheduleIds }.flatten().distinct().size != newAssistantScheduleDto.map { it.scheduleIds }.flatten().size) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Schedules are repeated", "Los horarios se repiten")
        }
        // Validate that the assistants are unique
        if (newAssistantScheduleDto.map { it.assistantId }.distinct().size != newAssistantScheduleDto.size) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Assistants are repeated", "Los auxiliares se repiten")
        }
        // Validate that there are exactly 6 assistants and that they are unique
        if (newAssistantScheduleDto.size != 6) throw EtnException(HttpStatus.BAD_REQUEST, "Error: Exactly 6 assistants are required", "Se requieren exactamente 6 auxiliares")
        // Validate that each assistant has exactly 3 schedules
        if (newAssistantScheduleDto.any { it.scheduleIds.size != 3 }) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Exactly 3 half-time schedules are required", "Se requieren exactamente 3 horarios de medio tiempo por auxiliar")
        }
        val scheduleIds = newAssistantScheduleDto.map { it.scheduleIds }.flatten()
        // Validate that schedules are in the range from 1 to 20
        if (scheduleIds.any { it < 1 || it > 20 }) {
            throw EtnException(HttpStatus.NOT_FOUND, "Error: Schedule not found", "Horario no encontrado")
        }
        // Validate that there are no repeated schedules
        if (scheduleIds.distinct().size != scheduleIds.size) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: Schedules are repeated", "Los horarios se repiten")
        }
        // Validate that at least ids from 1 to 15 are present
        if (!scheduleIds.containsAll((1..15).toList())) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: It does not meet the required schedules", "No cumple con los horarios requeridos")
        }
        // Validate that at least 3 ids from 16 to 20 are present
        if (scheduleIds.filter { it in (16..20) }.size < 3) {
            throw EtnException(HttpStatus.BAD_REQUEST, "Error: It does not meet the required schedules", "No cumple con los horarios requeridos")
        }
        // Validate that the abs difference between the schedules is at least 4
        newAssistantScheduleDto.forEach { newAssistantSchedule ->
            if(!isValidScheduleIds(newAssistantSchedule.scheduleIds)) {
                throw EtnException(HttpStatus.BAD_REQUEST, "Error: The schedules are too close", "Los horarios se superponen")
            }
        }
        // Validate the semester exists
        semesterRepository.findBySemesterIdAndStatusIsTrue(semesterId)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: Semester not found", "Semestre no encontrado")
        // Validate the assistants exists
        val assistantEntities = assistantRepository.findAllByAssistantIdInAndStatusIsTrue(newAssistantScheduleDto.map { it.assistantId.toInt() })
        if (assistantEntities.size != newAssistantScheduleDto.size) throw EtnException(HttpStatus.NOT_FOUND, "Error: Assistant not found", "Al menos un auxiliar no fue encontrado")

        // Delete previous assistant schedules changing the status to false
        logger.info("Deleting previous assistant schedules")
        val assistantScheduleEntities = assistantScheduleRepository.findAllBySemesterIdAndStatusIsTrue(semesterId)
        assistantScheduleEntities.forEach {
            it.status = false
            assistantScheduleRepository.save(it)
        }
        // Add the assistant schedules to the semester
        logger.info("Adding new assistant schedules")

        newAssistantScheduleDto.forEach { newAssistantSchedule ->
            newAssistantSchedule.scheduleIds.forEach { scheduleId ->
                val assistantScheduleEntity = AssistantSchedule()
                assistantScheduleEntity.assistantId = newAssistantSchedule.assistantId
                assistantScheduleEntity.scheduleId = scheduleId
                assistantScheduleEntity.semesterId = semesterId.toInt()
                assistantScheduleRepository.save(assistantScheduleEntity)
            }
        }
    }

    fun addRandomAssistantsScheduleToSemester(
        semesterId: Long,
    ) {
        // Validate the semester exists
        semesterRepository.findBySemesterIdAndStatusIsTrue(semesterId)
            ?: throw EtnException(HttpStatus.NOT_FOUND, "Error: Semester not found", "Semestre no encontrado")
        // Get the assistants from the semester
        val assistantEntities = assistantRepository.findAllBySemesterIdAndStatusIsTrue(semesterId)
        // Validate that there are exactly 6 assistants
        if (assistantEntities.size != 6) throw EtnException(HttpStatus.BAD_REQUEST, "Error: Exactly 6 assistants are required", "Se requieren exactamente 6 auxiliares")
        // Delete previous assistant schedules changing the status to false
        logger.info("Deleting previous assistant schedules, for random assignment")
        val assistantScheduleEntities = assistantScheduleRepository.findAllBySemesterIdAndStatusIsTrue(semesterId)
        assistantScheduleEntities.forEach {
            it.status = false
            assistantScheduleRepository.save(it)
        }

        logger.info("Adding new assistant schedules, for random assignment")
        val scheduleIds = (1..15).toList().shuffled() + (16..20).toList().shuffled().take(3)
        val usedScheduleIds = mutableSetOf<Int>()

        assistantEntities.forEach { assistantEntity ->
            var selectedScheduleIds: List<Int>
            do {
                selectedScheduleIds = scheduleIds.filterNot { it in usedScheduleIds }.shuffled().take(3)
            } while (!isValidScheduleIds(selectedScheduleIds))
            usedScheduleIds.addAll(selectedScheduleIds)
            selectedScheduleIds.forEach { scheduleId ->
                val assistantScheduleEntity = AssistantSchedule()
                assistantScheduleEntity.assistantId = assistantEntity.assistantId
                assistantScheduleEntity.scheduleId = scheduleId
                assistantScheduleEntity.semesterId = semesterId.toInt()
                assistantScheduleRepository.save(assistantScheduleEntity)
            }
        }
    }

    fun isValidScheduleIds(scheduleIds: List<Int>): Boolean {
        val group1 = scheduleIds.filter { it in 1..10 }
        val group2 = scheduleIds.filter { it in 11..20 }

        for (i in group1.indices) {
            for (j in i + 1 until group1.size) {
                if (abs(group1[i] - group1[j]) == 5) {
                    return false
                }
            }
        }

        for (i in group2.indices) {
            for (j in i + 1 until group2.size) {
                if (abs(group2[i] - group2[j]) == 5) {
                    return false
                }
            }
        }

        return true
    }
}