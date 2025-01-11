package ru.dozen.mephi.meta.web.model.project;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRoleRequestDTO implements Serializable {

    @NotNull
    private Long userId;

    @NotNull
    private Long oldRoleId;

    @NotNull
    private Long newRoleId;
}
