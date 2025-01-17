package ru.dozen.mephi.meta.client.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TestType {
    TEST_CASE("Ситуативный тест"), TEST_PLAN("Плановый тест");
    private final String description;
}
