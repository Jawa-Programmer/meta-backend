package ru.dozen.mephi.meta.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserState {
    ACTIVE("активный"),
    BLOCKED("заблокирован");

    private final String state;
}
