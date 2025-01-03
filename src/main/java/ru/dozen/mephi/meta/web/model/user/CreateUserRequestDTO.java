package ru.dozen.mephi.meta.web.model.user;

import jakarta.validation.constraints.NotBlank;
import java.util.EnumSet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.dozen.mephi.meta.domain.enums.SystemRole;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequestDTO {

    @NotBlank
    private String login;
    @NotBlank
    private String password;

    private String fio;

    private String picturePath;

    private EnumSet<SystemRole> systemRoles;
}
