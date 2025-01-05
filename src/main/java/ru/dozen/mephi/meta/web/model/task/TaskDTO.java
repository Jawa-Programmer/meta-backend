package ru.dozen.mephi.meta.web.model.task;

import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.dozen.mephi.meta.domain.enums.TaskState;
import ru.dozen.mephi.meta.web.model.comment.CommentDTO;
import ru.dozen.mephi.meta.web.model.project.ProjectDTO;
import ru.dozen.mephi.meta.web.model.user.UserDTO;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO implements Serializable {

    private Long id;

    private String key;

    private String title;

    private String description;

    private Boolean isTestingRequired;

    private UserDTO author;

    private UserDTO executor;

    private ProjectDTO project;

    private TaskState taskState;

    // TODO: заменить на Enum или класс, когда спишемся с АСУТ
    private String testStatus;

    private List<CommentDTO> comments;

    private List<UserDTO> watchers;
}
