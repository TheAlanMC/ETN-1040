package bo.edu.umsa.backend.mapper

import bo.edu.umsa.backend.dto.ReportDto
import bo.edu.umsa.backend.entity.Report


class ReportMapper {
    companion object {
        fun entityToDto(report: Report): ReportDto {
            return ReportDto(
                reportId = report.reportId,
                user = UserPartialMapper.entityToDto(report.user!!),
                file = FilePartialMapper.entityToDto(report.file!!),
                reportName = report.reportName,
                reportType = report.reportType.toString().lowercase().replaceFirstChar { it.uppercase() },
                reportStartDate = report.reportStartDate,
                reportEndDate = report.reportEndDate,
                txDate = report.txDate,
            )
        }
    }
}
