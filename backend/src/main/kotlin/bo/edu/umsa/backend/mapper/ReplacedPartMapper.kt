package bo.edu.umsa.backend.mapper

import bo.edu.umsa.backend.dto.ReplacedPartDto
import bo.edu.umsa.backend.entity.ReplacedPart


class ReplacedPartMapper {
    companion object {
        fun entityToDto(replacedPart: ReplacedPart): ReplacedPartDto {
            return ReplacedPartDto(
                replacedPartId = replacedPart.replacedPartId,
                replacedPartDescription = replacedPart.replacedPartDescription,
                txDate = replacedPart.txDate,
                replacedPartFiles = replacedPart.replacedPartFiles?.filter { it.status }?.map { FilePartialMapper.entityToDto(it.file!!) }
                    ?: emptyList(),
            )
        }
    }
}