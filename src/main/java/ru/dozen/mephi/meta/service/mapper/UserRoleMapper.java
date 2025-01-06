package ru.dozen.mephi.meta.service.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.dozen.mephi.meta.domain.UserRole;
import ru.dozen.mephi.meta.web.model.role.UserRoleDTO;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserRoleMapper {

    UserRoleDTO toDto(UserRole user);

    List<UserRoleDTO> toDto(List<UserRole> users);

    UserRole toEntity(UserRoleDTO dto);

    @Mapping(target = "id", ignore = true)
    void updateUserRole(@MappingTarget UserRole target, UserRoleDTO source);

}
