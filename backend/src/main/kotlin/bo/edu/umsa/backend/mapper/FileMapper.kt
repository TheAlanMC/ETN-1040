package bo.edu.umsa.backend.mapper

import bo.edu.umsa.backend.dto.FileDto
import bo.edu.umsa.backend.entity.File

class FileMapper {
    companion object {
        fun entityToDto(file: File): FileDto {
            return FileDto(
                filename = file.filename,
                contentType = file.contentType,
                fileData = file.fileData,
                thumbnail = file.thumbnail
            )
        }
    }
}