package bo.edu.umsa.backend.mapper

import bo.edu.umsa.backend.dto.ToolDto
import bo.edu.umsa.backend.entity.Tool

class ToolMapper {
    companion object {
        fun entityToDto(tool: Tool): ToolDto {
            return ToolDto(toolId = tool.toolId, filePhotoId = tool.filePhotoId, toolCode = tool.toolCode, toolName = tool.toolName, toolDescription = tool.toolDescription, available = tool.available)
        }
    }
}