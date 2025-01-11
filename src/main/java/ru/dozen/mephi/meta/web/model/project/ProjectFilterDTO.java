package ru.dozen.mephi.meta.web.model.project;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.dozen.mephi.meta.util.filter.EntityFieldName;
import ru.dozen.mephi.meta.util.filter.Filter;
import ru.dozen.mephi.meta.util.filter.StringFilter;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectFilterDTO {

    private StringFilter title;
    private Filter<String> directorLogin;
    @EntityFieldName("director.fio")
    private StringFilter directorFio;
    private Filter<String> state;
}
