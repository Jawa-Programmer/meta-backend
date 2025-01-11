package ru.dozen.mephi.meta.web.model.project;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantsDTO {

    private Long userId;
    private String userName;
    private String userLogin;
    private String roleName;
}
