package ru.dozen.mephi.meta.service;

import java.util.List;
import ru.dozen.mephi.meta.web.model.role.UserRoleDTO;

public interface UserRolesService {

    List<UserRoleDTO> getAllUserRoles();

    UserRoleDTO createUserRole(UserRoleDTO userRole);

    UserRoleDTO updateUserRole(UserRoleDTO userRole);

    void deleteUserRole(long id);
}
