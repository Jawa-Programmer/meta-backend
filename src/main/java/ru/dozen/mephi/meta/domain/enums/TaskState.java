package ru.dozen.mephi.meta.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TaskState {

    NEW("новая"),
    ASSIGNED("назначена"),
    IN_PROGRESS("в процессе"),
    DONE("выполнена"),
    CLOSED("закрыта"),
    CANCELED("отменена");

    private final String state;
}
