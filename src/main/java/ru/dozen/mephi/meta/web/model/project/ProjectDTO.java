package ru.dozen.mephi.meta.web.model.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.dozen.mephi.meta.domain.enums.ProjectState;
import ru.dozen.mephi.meta.web.model.user.UserDTO;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDTO implements Serializable {

    private Long id;

    @NotBlank
    private String title;

    @NotNull
    private ProjectState state;

    private UserDTO director;

}