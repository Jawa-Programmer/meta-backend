package ru.dozen.mephi.meta.web.model.comment;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.dozen.mephi.meta.web.model.user.UserDTO;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO implements Serializable {

    private Long id;
    private UserDTO author;
    private String text;
}
