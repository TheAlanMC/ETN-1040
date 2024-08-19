package bo.edu.umsa.backend.mapper

import bo.edu.umsa.backend.dto.SemesterDto
import bo.edu.umsa.backend.entity.Semester

class SemesterMapper {
    companion object {
        fun entityToDto(semester:Semester):SemesterDto{
            return SemesterDto(
                semesterId = semester.semesterId,
                semesterName = semester.semesterName,
                semesterDateFrom = semester.semesterDateFrom,
                semesterDateTo = semester.semesterDateTo,
            )
        }
    }
}