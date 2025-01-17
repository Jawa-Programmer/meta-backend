package ru.dozen.mephi.meta.service.impl;

import static ru.dozen.mephi.meta.util.ProblemUtils.badRequest;

import java.util.EnumSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
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
            throw ProblemUtils.forbidden("Создание, назначение и редактирование суперпользователей запрещено");
        }
        if (roles.contains(SystemRole.ADMIN) && !AuthoritiesUtils.isSuperUser()) {
            throw ProblemUtils.forbidden("Текущий пользователь не имеет прав создавать, назначать или редактировать администраторов");
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
            throw badRequest("Пользователь с данным логином уже существует");
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
        var users = usersRepository.findAll(specification);
        var projectId = filter.getProjectId();
        var roles = filter.getHasAnySystemRole();
        if (roles != null && !roles.isEmpty()) {
            users = users.stream()
                    .filter(u -> CollectionUtils.containsAny(u.getSystemRoles(), roles))
                    .toList();
        }
        if (projectId != null) {
            users = users.stream()
                    .filter(u -> AuthoritiesUtils.isMemberOfProject(u, projectId))
                    .toList();
        }
        return userMapper.toDto(users);
    }
}
