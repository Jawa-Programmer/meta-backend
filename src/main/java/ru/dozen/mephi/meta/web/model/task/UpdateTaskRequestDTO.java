package ru.dozen.mephi.meta.web.model.task;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.dozen.mephi.meta.web.model.user.UserDTO;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTaskRequestDTO implements Serializable {

    private String title;

    private String description;

    private Boolean isTestingRequired;

    private UserDTO executor;
}
