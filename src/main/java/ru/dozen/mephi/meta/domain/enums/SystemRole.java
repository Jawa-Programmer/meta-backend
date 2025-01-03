package ru.dozen.mephi.meta.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Системная роль пользователя
 */
@Getter
@RequiredArgsConstructor
public enum SystemRole {
    USER("Пользователь"), ADMIN("Администратор"), SUPERUSER("Супер-пользовать");
    private final String title;
}
