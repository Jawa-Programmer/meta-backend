package ru.dozen.mephi.meta.service.mapper;

import ru.dozen.mephi.meta.domain.RoleRecord;
import ru.dozen.mephi.meta.web.model.project.RoleRecordDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleRecordMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.fio", target = "userFio")
    @Mapping(source = "role.id", target = "roleId")
    @Mapping(source = "role.roleName", target = "roleName")
    @Mapping(source = "project.id", target = "projectId")
    RoleRecordDTO toDto(RoleRecord roleRecord);
}