package ru.dozen.mephi.meta.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import ru.dozen.mephi.meta.domain.User;

@UtilityClass
public class AuthoritiesUtils {

    public boolean isUser() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(it -> StringUtils.equals(it, "ROLE_USER"));
    }

    public boolean isAdmin() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(it -> StringUtils.equals(it, "ROLE_ADMIN"));
    }

    public boolean isSuperUser() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(it -> StringUtils.equals(it, "ROLE_SUPERUSER"));
    }

    public User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public boolean hasAnyRole(UserDetails user, String... roles) {
        if (user == null) {
            return false;
        }
        if (roles == null || roles.length == 0) {
            return true;
        }
        for (String role : roles) {
            if (user.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(authority -> StringUtils.equals(authority, role))) {
                return true;
            }
        }
        return false;
    }

    public boolean isDirectorOfProject(User user, long projectId) {
        if (user.getProjects() == null) {
            return false;
        }
        return user.getProjects().stream().anyMatch(project -> project.getId().equals(projectId));
    }

    public boolean isAssignedToProject(User user, long projectId) {
        if (user.getRoleRecords() == null) {
            return false;
        }
        return user.getRoleRecords().stream().anyMatch(rec -> rec.getProject().getId().equals(projectId));
    }

    public boolean isMemberOfProject(User user, long projectId) {
        return user.isEnabled() && (isDirectorOfProject(user, projectId) || isAssignedToProject(user, projectId));
    }

    public boolean isMemberOfProjectAndHasAnyRole(UserDetails userDetails, long projectId, String... roles) {
        if (userDetails instanceof User user) {
            return hasAnyRole(user, roles) && isMemberOfProject(user, projectId);
        }
        return false;
    }
}
