package ru.dozen.mephi.meta.util;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import ru.dozen.mephi.meta.util.filter.EntityFieldName;
import ru.dozen.mephi.meta.util.filter.Filter;

@UtilityClass
public class FilterUtils {

    @SneakyThrows
    private String getName(Class<?> clazz, Method getter) {
        var fieldName = StringUtils.uncapitalize(getter.getName().substring(3));
        var field = clazz.getDeclaredField(fieldName);
        var annotation = field.getAnnotation(EntityFieldName.class);
        if (annotation != null) {
            return annotation.value();
        }
        return fieldName;
    }

    @SneakyThrows
    @SuppressWarnings("rawtypes")
    private static Filter getFilter(Object obj, Method getter) {
        return (Filter) getter.invoke(obj);
    }

    private <T, V> Specification<T> toSpecification(Filter<V> f, Class<?> clazz, Method getter) {
        if (f != null) {
            return f.toSpecification(getName(clazz, getter));
        }
        return null;
    }

    // TODO переписать, чтобы фильтры генерировались на этапе компиляции, а не использовали рефлексию в runtime
    @SuppressWarnings("unchecked")
    public <T, E> Specification<E> toSpecification(T filter) {
        if (filter == null) {
            return null;
        }
        var clazz = filter.getClass();
        return Arrays.stream(clazz.getMethods())
                .filter(method -> Filter.class.isAssignableFrom(method.getReturnType()))
                .filter(method -> method.getName().startsWith("get"))
                .map(method -> FilterUtils.toSpecification(getFilter(filter, method), clazz, method))
                .filter(Objects::nonNull)
                .reduce(Specification::and)
                .orElse(null);
    }
}
