package ru.dozen.mephi.meta.web.model.task;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.dozen.mephi.meta.domain.enums.ProjectState;
import ru.dozen.mephi.meta.web.model.user.UserDTO;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectShortInfoDTO implements Serializable {

    private Long id;

    private String title;

    private ProjectState projectState;

    private UserDTO director;
}
