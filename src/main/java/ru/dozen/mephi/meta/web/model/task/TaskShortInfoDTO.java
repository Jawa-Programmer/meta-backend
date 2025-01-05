package ru.dozen.mephi.meta.web.model.task;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.dozen.mephi.meta.domain.enums.TaskState;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskShortInfoDTO implements Serializable {

    private Long id;

    private String key;

    private String title;

    private TaskState taskState;
}
