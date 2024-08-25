package bo.edu.umsa.backend.dto

data class ReportOptions(
    val excludeBuiltStyles: Boolean,
    val landscape: Boolean,
    val margins: Margins,
    val pageFormat: String,
)

data class Margins(
    val top: Int,
    val right: Int,
    val bottom: Int,
    val left: Int,
)
