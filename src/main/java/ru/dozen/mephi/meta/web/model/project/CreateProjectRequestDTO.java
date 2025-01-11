package ru.dozen.mephi.meta.web.model.project;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.dozen.mephi.meta.web.model.user.UserDTO;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProjectRequestDTO implements Serializable {

    @NotBlank
    private String title;

    private UserDTO director;
}