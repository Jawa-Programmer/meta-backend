package ru.dozen.mephi.meta.web.model.project;

import lombok.Data;

import java.io.Serializable;

@Data
public class RoleRecordDTO implements Serializable {
    private Long id;
    private Long userId;
    private String userFio;
    private Long roleId;
    private String roleName;
    private Long projectId;
}
