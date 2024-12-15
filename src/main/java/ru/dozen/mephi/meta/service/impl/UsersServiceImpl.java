package ru.dozen.mephi.meta.service.impl;

import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.dozen.mephi.meta.repository.UsersRepository;
import ru.dozen.mephi.meta.service.UsersService;
import ru.dozen.mephi.meta.service.mapper.UserMapper;
import ru.dozen.mephi.meta.web.model.UserDTO;

@Service
@RequiredArgsConstructor
public class UsersServiceImpl implements UsersService {

    private final UsersRepository usersRepository;
    private final UserMapper userMapper;

    @Override
    public UserDTO getUserByLogin(String login) {
        var user = usersRepository.findByLogin(login).map(userMapper::toDto);
        return user.orElseThrow(() -> new NoSuchElementException("User not found"));
    }
}
