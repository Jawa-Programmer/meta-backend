package ru.dozen.mephi.meta.web.model.task;

import java.io.Serializable;
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
public class TaskFilterDTO implements Serializable {

    private StringFilter key;
    private Filter<Boolean> isTestingRequired;
    private StringFilter title;
    private StringFilter description;
    @EntityFieldName("author.login")
    private StringFilter authorLogin;
    @EntityFieldName("author.fio")
    private StringFilter authorFio;
    @EntityFieldName("executor.login")
    private StringFilter executorLogin;
    @EntityFieldName("executor.fio")
    private StringFilter executorFio;
}
