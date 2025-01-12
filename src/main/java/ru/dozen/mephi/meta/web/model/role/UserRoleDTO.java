package ru.dozen.mephi.meta.web.model.role;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleDTO implements Serializable {

    private Long id;

    @NotBlank
    private String roleName;

    private String description;
}
