package ru.dozen.mephi.meta;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import ru.dozen.mephi.meta.repository.ProjectsRepository;
import ru.dozen.mephi.meta.repository.TasksRepository;
import ru.dozen.mephi.meta.repository.UserRolesRepository;
import ru.dozen.mephi.meta.repository.UsersRepository;
import ru.dozen.mephi.meta.security.AuthRequest;

@AutoConfigureMockMvc
public class AbstractIntegrationTest extends AbstractTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected PasswordEncoder encoder;

    @Autowired
    protected UsersRepository usersRepository;

    @Autowired
    protected TasksRepository tasksRepository;

    @Autowired
    protected ProjectsRepository projectsRepository;

    @Autowired
    protected UserRolesRepository rolesRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    void clearDatabase() {
        jdbcTemplate.execute("TRUNCATE projects CASCADE;");
        jdbcTemplate.execute("TRUNCATE users CASCADE;");
        jdbcTemplate.execute("TRUNCATE user_roles CASCADE;");
    }

    @SneakyThrows
    protected HttpHeaders auth(String login, String password) {
        AuthRequest authRequest = AuthRequest.builder()
                .username(login)
                .password(password)
                .build();
        var headers = new HttpHeaders();
        var token = mockMvc.perform(post("/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        headers.add("Authorization", "Bearer " + token);
        return headers;
    }
}
