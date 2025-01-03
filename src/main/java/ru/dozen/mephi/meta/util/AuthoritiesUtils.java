package ru.dozen.mephi.meta.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

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
}
