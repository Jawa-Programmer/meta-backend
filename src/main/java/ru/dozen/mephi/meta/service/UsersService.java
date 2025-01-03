package ru.dozen.mephi.meta.service;

import java.util.List;
import ru.dozen.mephi.meta.web.model.user.CreateUserRequestDTO;
import ru.dozen.mephi.meta.web.model.user.UpdateUserRequestDTO;
import ru.dozen.mephi.meta.web.model.user.UserDTO;
import ru.dozen.mephi.meta.web.model.user.UserFilterDTO;

public interface UsersService {

    UserDTO getUserByLogin(String login);

    UserDTO registerNewUser(CreateUserRequestDTO rq);

    UserDTO updateUser(Long id, UpdateUserRequestDTO rq);

    UserDTO setPassword(String login, String password);

    List<UserDTO> searchByFilter(UserFilterDTO filter);
}
