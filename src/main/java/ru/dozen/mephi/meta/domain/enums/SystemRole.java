package ru.dozen.mephi.meta.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Системная роль пользователя
 */
@Getter
@RequiredArgsConstructor
public enum SystemRole {
    USER("Пользователь"), ADMIN("Администратор");
    private final String title;
}
