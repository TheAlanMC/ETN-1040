package bo.edu.umsa.backend.service

import bo.edu.umsa.backend.dto.ScheduleDto
import bo.edu.umsa.backend.mapper.ScheduleMapper
import bo.edu.umsa.backend.repository.ScheduleRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ScheduleService @Autowired constructor(
    private val scheduleRepository: ScheduleRepository,
) {
    companion object {
        private val logger = LoggerFactory.getLogger(ScheduleService::class.java)
    }

    fun getAllSchedules(): List<ScheduleDto> {
        logger.info("Getting all schedules")
        val scheduleEntities = scheduleRepository.findAllByStatusIsTrueOrderByScheduleId()
        return scheduleEntities.map { ScheduleMapper.entityToDto(it) }
    }
}