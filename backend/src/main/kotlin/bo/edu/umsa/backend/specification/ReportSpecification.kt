package bo.edu.umsa.backend.specification

import bo.edu.umsa.backend.entity.Report
import org.springframework.data.jpa.domain.Specification
import java.util.*


class ReportSpecification {
    companion object {

        fun dateBetween(
            dateFrom: Date,
            dateTo: Date
        ): Specification<Report> {
            return Specification { root, _, cb ->
                cb.between(root.get("txDate"), dateFrom, dateTo)
            }
        }

        fun statusIsTrue(): Specification<Report> {
            return Specification { root, _, cb ->
                cb.equal(root.get<Report>("status"), true)
            }
        }
    }
}