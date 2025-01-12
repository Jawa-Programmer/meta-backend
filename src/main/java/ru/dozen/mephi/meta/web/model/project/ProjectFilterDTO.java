package ru.dozen.mephi.meta.web.model.project;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.dozen.mephi.meta.domain.enums.ProjectState;
import ru.dozen.mephi.meta.util.filter.EntityFieldName;
import ru.dozen.mephi.meta.util.filter.Filter;
import ru.dozen.mephi.meta.util.filter.StringFilter;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectFilterDTO implements Serializable {

    private StringFilter title;
    @EntityFieldName("director.login")
    private StringFilter directorLogin;
    @EntityFieldName("director.fio")
    private StringFilter directorFio;
    private Filter<ProjectState> state;
}
