package bo.edu.umsa.backend.dto

import java.time.LocalDate

data class SemesterDto(
    val semesterId: Int,
    val semesterName: String,
    val semesterDateFrom: LocalDate,
    val semesterDateTo: LocalDate,
)
