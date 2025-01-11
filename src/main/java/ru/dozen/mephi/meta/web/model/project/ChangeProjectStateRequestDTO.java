package ru.dozen.mephi.meta.web.model.project;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.dozen.mephi.meta.domain.enums.ProjectState;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangeProjectStateRequestDTO implements Serializable {

    @NotNull
    private ProjectState state;
}
