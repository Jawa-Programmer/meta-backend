package ru.dozen.mephi.meta.web.model.project;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.dozen.mephi.meta.web.model.user.UserDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantsDTO implements Serializable {

    @Schema(deprecated = true)
    @Deprecated(forRemoval = true)
    private Long userId;
    @Schema(deprecated = true)
    @Deprecated(forRemoval = true)
    private String userName;
    @Schema(deprecated = true)
    @Deprecated(forRemoval = true)
    private String userLogin;
    private UserDTO user;
    private String roleName;
}
