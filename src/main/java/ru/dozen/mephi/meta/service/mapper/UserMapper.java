package ru.dozen.mephi.meta.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.dozen.mephi.meta.domain.User;
import ru.dozen.mephi.meta.web.model.UserDTO;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "userState", target = "state")
    UserDTO toDto(User user);
}
