package ru.dozen.mephi.meta.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProjectState {
    ACTIVE("активный"),
    CLOSED("закрытый");

    private final String state;
}
