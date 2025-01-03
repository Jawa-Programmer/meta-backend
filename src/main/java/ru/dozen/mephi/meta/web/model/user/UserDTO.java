package ru.dozen.mephi.meta.web.model.user;

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
public class UserDTO {

    private Long id;
    private String login;
    private String fio;
    private String picturePath;
    private UserState state;
    private EnumSet<SystemRole> systemRoles;

}
