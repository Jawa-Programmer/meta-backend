package ru.dozen.mephi.meta.web.model.user;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.EnumSet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.dozen.mephi.meta.domain.enums.SystemRole;
import ru.dozen.mephi.meta.domain.enums.UserState;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequestDTO implements Serializable {

    @NotBlank
    private String login;

    private String fio;

    private EnumSet<SystemRole> systemRoles;

    private String picturePath;

    private UserState state;
}
