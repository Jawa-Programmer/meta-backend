package ru.dozen.mephi.meta.web.model.project;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantsDTO implements Serializable {

    private Long userId;
    private String userName;
    private String userLogin;
    private String roleName;
}
