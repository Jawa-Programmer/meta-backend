package ru.dozen.mephi.meta.util.filter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Позволяет изменить имя поля в сущности. По умолчанию считается, что имя поля в сущности БД совпадает с именем поля в
 * фильтре
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EntityFieldName {

    String value();
}
