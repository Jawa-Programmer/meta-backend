package ru.dozen.mephi.meta.service;

import ru.dozen.mephi.meta.web.model.UserDTO;

public interface UsersService {

    UserDTO getUserByLogin(String login);
}
