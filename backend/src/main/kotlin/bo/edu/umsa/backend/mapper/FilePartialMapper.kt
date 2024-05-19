package bo.edu.umsa.backend.mapper

import bo.edu.umsa.backend.dto.FilePartialDto
import bo.edu.umsa.backend.entity.File

class FilePartialMapper {
    companion object {
        fun entityToDto(file: File): FilePartialDto {
            return FilePartialDto(fileId = file.fileId, filename = file.filename, contentType = file.contentType, fileSize = file.fileSize)
        }
    }
}