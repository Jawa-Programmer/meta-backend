package ru.dozen.mephi.meta.web.model.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.dozen.mephi.meta.web.model.user.UserDTO;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTaskRequestDTO implements Serializable {

    @Pattern(regexp = "[a-zA-Z][a-zA-Z0-9\\-_]{9}")
    private String key;

    @NotBlank
    private String title;

    private String description;

    private Boolean isTestingRequired;

    private UserDTO executor;

    private List<UserDTO> watchers = Collections.emptyList();
}