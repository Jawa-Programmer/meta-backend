package ru.dozen.mephi.meta.service.impl;

import java.util.EnumSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.dozen.mephi.meta.domain.User;
import ru.dozen.mephi.meta.domain.enums.SystemRole;
import ru.dozen.mephi.meta.repository.UsersRepository;
import ru.dozen.mephi.meta.service.UsersService;
import ru.dozen.mephi.meta.service.mapper.UserMapper;
import ru.dozen.mephi.meta.util.AuthoritiesUtils;
import ru.dozen.mephi.meta.util.FilterUtils;
import ru.dozen.mephi.meta.util.ProblemUtils;
import ru.dozen.mephi.meta.web.model.user.CreateUserRequestDTO;
import ru.dozen.mephi.meta.web.model.user.UpdateUserRequestDTO;
import ru.dozen.mephi.meta.web.model.user.UserDTO;
import ru.dozen.mephi.meta.web.model.user.UserFilterDTO;


@Slf4j
@Service
@RequiredArgsConstructor
public class UsersServiceImpl implements UsersService {

    private final UsersRepository usersRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    private static final String USER_NOT_FOUND_MESSAGE = "User not found";


    private static void checkRights(EnumSet<SystemRole> roles) {
        if (roles == null || roles.isEmpty()) {
            return;
        }
        if (roles.contains(SystemRole.SUPERUSER)) {
            throw ProblemUtils.forbidden("Assigning and editing superusers are forbidden");
        }
        if (roles.contains(SystemRole.ADMIN) && !AuthoritiesUtils.isSuperUser()) {
            throw ProblemUtils.forbidden("Only superuser can assign or edit administrator");
        }
    }

    @Override
    public UserDTO getUserByLogin(String login) {
        var user = usersRepository.findByLogin(login).map(userMapper::toDto);
        return user.orElseThrow(() -> ProblemUtils.notFound(USER_NOT_FOUND_MESSAGE));
    }

    @Override
    public UserDTO registerNewUser(CreateUserRequestDTO rq) {
        checkRights(rq.getSystemRoles());

        var user = userMapper.fromCreateRequest(rq);
        user.setPasswordHash(passwordEncoder.encode(rq.getPassword()));
        try {
            return userMapper.toDto(usersRepository.save(user));
        } catch (DataIntegrityViolationException e) {
            throw ProblemUtils.badRequest("Login already exists");
        }
    }

    @Override
    public UserDTO updateUser(Long id, UpdateUserRequestDTO rq) {
        checkRights(rq.getSystemRoles());

        var user = usersRepository.findById(id)
                .orElseThrow(() -> ProblemUtils.notFound(USER_NOT_FOUND_MESSAGE));

        userMapper.updateUser(user, rq);
        return userMapper.toDto(usersRepository.save(user));
    }

    @Override
    public UserDTO setPassword(String login, String password) {
        var user = usersRepository.findByLogin(login)
                .orElseThrow(() -> ProblemUtils.notFound(USER_NOT_FOUND_MESSAGE));
        checkRights(user.getSystemRoles());

        user.setPasswordHash(passwordEncoder.encode(password));
        return userMapper.toDto(usersRepository.save(user));
    }

    @Override
    public List<UserDTO> searchByFilter(UserFilterDTO filter) {
        Specification<User> specification = Specification.where(FilterUtils.toSpecification(filter));
        return userMapper.toDto(usersRepository.findAll(specification));
    }
}
