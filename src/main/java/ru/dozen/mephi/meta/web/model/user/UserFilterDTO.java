package ru.dozen.mephi.meta.web.model.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.dozen.mephi.meta.domain.enums.UserState;
import ru.dozen.mephi.meta.util.filter.EntityFieldName;
import ru.dozen.mephi.meta.util.filter.Filter;
import ru.dozen.mephi.meta.util.filter.StringFilter;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserFilterDTO {

    private StringFilter login;
    private StringFilter fio;
    @EntityFieldName("userState")
    private Filter<UserState> state;

}
