package ru.dozen.mephi.meta.domain.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

/**
 * Системная роль пользователя
 */
@Getter
@RequiredArgsConstructor
public enum SystemRole implements GrantedAuthority {
    @Schema(description = "Пользователь системы")
    USER("Пользователь"),
    @Schema(description = "Администратор системы")
    ADMIN("Администратор"),
    @Schema(description = "Супер-пользователь. Пользователь с правом назначать администраторов")
    SUPERUSER("Супер-пользовать"),
    @Schema(description = "Служебный пользователь. Используется для организации взаимодействия со сторонними системами")
    SERVICE("Служебный");
    private final String title;
    private final String authority = "ROLE_" + this;
}
