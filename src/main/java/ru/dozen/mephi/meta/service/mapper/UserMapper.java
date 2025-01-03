package ru.dozen.mephi.meta.service.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.dozen.mephi.meta.domain.User;
import ru.dozen.mephi.meta.web.model.user.CreateUserRequestDTO;
import ru.dozen.mephi.meta.web.model.user.UpdateUserRequestDTO;
import ru.dozen.mephi.meta.web.model.user.UserDTO;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    @Mapping(source = "userState", target = "state")
    UserDTO toDto(User user);

    List<UserDTO> toDto(List<User> user);

    @Mapping(target = "userState", constant = "ACTIVE")
    User fromCreateRequest(CreateUserRequestDTO createUserRequestDTO);

    @Mapping(source = "state", target = "userState")
    void updateUser(@MappingTarget User target, UpdateUserRequestDTO source);

}
