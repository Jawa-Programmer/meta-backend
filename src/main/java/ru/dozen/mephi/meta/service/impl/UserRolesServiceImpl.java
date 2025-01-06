package ru.dozen.mephi.meta.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.dozen.mephi.meta.repository.UserRolesRepository;
import ru.dozen.mephi.meta.service.UserRolesService;
import ru.dozen.mephi.meta.service.mapper.UserRoleMapper;
import ru.dozen.mephi.meta.util.ProblemUtils;
import ru.dozen.mephi.meta.web.model.role.UserRoleDTO;

@Service
@RequiredArgsConstructor
public class UserRolesServiceImpl implements UserRolesService {

    private final UserRolesRepository userRolesRepository;
    private final UserRoleMapper userRoleMapper;

    @Override
    public List<UserRoleDTO> getAllUserRoles() {
        return userRoleMapper.toDto(userRolesRepository.findAll());
    }

    @Override
    public UserRoleDTO createUserRole(UserRoleDTO userRole) {
        var entity = userRolesRepository.save(userRoleMapper.toEntity(userRole));
        return userRoleMapper.toDto(entity);
    }

    @Override
    public UserRoleDTO updateUserRole(UserRoleDTO userRole) {
        var entity = userRolesRepository.findById(userRole.getId())
                .orElseThrow(() -> ProblemUtils.notFound("Not found role with id " + userRole.getId()));
        userRoleMapper.updateUserRole(entity, userRole);
        return userRoleMapper.toDto(userRolesRepository.save(entity));
    }

    @Override
    public void deleteUserRole(long id) {
        try {
            userRolesRepository.deleteById(id);
        } catch (Exception e) {
            throw ProblemUtils.badRequest("Cannot delete user role with id " + id);
        }
    }
}
