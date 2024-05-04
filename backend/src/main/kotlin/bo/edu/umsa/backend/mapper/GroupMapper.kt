package bo.edu.umsa.backend.mapper

import bo.edu.umsa.backend.dto.GroupDto
import bo.edu.umsa.backend.entity.Group


class GroupMapper {
    companion object {
        fun entityToDto(group: Group): GroupDto {
            return GroupDto(
                groupId = group.groupId,
                groupName = group.groupName,
                groupDescription = group.groupDescription,
            )
        }
    }
}