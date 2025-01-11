package ru.dozen.mephi.meta.web.model.project;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
public class AssignRemoveParticipantRequestDTO implements Serializable {

    @NotNull
    private Long userId;

    @NotNull
    private Long roleId;
}
