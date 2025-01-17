package ru.dozen.mephi.meta.client.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TestStatus {
    SUCCESS("Тест пройден"), FAIL("Тест не пройден"), TO_DO("В процессе");
    private final String description;
}
