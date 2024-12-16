package ru.dozen.mephi.meta.domain.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import ru.dozen.mephi.meta.domain.enums.SystemRole;

@Converter
public class SystemRolesConverter implements AttributeConverter<EnumSet<SystemRole>, String> {

    private static final String SPLIT_CHAR = ",";

    @Override
    public String convertToDatabaseColumn(EnumSet<SystemRole> set) {
        return set == null ? null :
                set.stream()
                        .map(SystemRole::toString)
                        .collect(Collectors.joining(SPLIT_CHAR));
    }

    @Override
    public EnumSet<SystemRole> convertToEntityAttribute(String longString) {
        return StringUtils.isBlank(longString) ? null :
                EnumSet.copyOf(Arrays.stream(longString.split(SPLIT_CHAR))
                        .map(this::fromString)
                        .toList());
    }

    private SystemRole fromString(String string) {
        return SystemRole.valueOf(string);
    }
}
