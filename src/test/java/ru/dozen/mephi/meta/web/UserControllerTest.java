package ru.dozen.mephi.meta.web;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.EnumSet;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import ru.dozen.mephi.meta.AbstractIntegrationTest;
import ru.dozen.mephi.meta.domain.User;
import ru.dozen.mephi.meta.domain.enums.SystemRole;
import ru.dozen.mephi.meta.domain.enums.UserState;
import ru.dozen.mephi.meta.security.AuthRequest;
import ru.dozen.mephi.meta.util.filter.Filter;
import ru.dozen.mephi.meta.util.filter.StringFilter;
import ru.dozen.mephi.meta.web.model.user.CreateUserRequestDTO;
import ru.dozen.mephi.meta.web.model.user.UpdateUserRequestDTO;
import ru.dozen.mephi.meta.web.model.user.UserDTO;
import ru.dozen.mephi.meta.web.model.user.UserFilterDTO;

class UserControllerTest extends AbstractIntegrationTest {

    private void assertCanAuth(String login, String password) throws Exception {
        AuthRequest authRequest = AuthRequest.builder()
                .username(login)
                .password(password)
                .build();

        mockMvc.perform(post("/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk());
    }

    private void assertCantAuth(String login, String password) throws Exception {
        AuthRequest authRequest = AuthRequest.builder()
                .username(login)
                .password(password)
                .build();

        mockMvc.perform(post("/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getUserByLogin_notAuthenticated() throws Exception {
        mockMvc.perform(get("/users/login"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void getUserByLogin_notFound() throws Exception {
        mockMvc.perform(get("/users/login"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void getUserByLogin_ok() throws Exception {
        var saved = usersRepository.save(User.builder()
                .login("login")
                .passwordHash(encoder.encode("password"))
                .userState(UserState.ACTIVE)
                .systemRoles(EnumSet.of(SystemRole.USER))
                .build()
        );
        final var excepted = UserDTO.builder()
                .id(saved.getId())
                .login(saved.getLogin())
                .fio(saved.getFio())
                .state(saved.getUserState())
                .systemRoles(saved.getSystemRoles())
                .build();

        final var exceptedString = objectMapper.writeValueAsString(excepted);

        mockMvc.perform(get("/users/login"))
                .andExpect(status().isOk())
                .andExpect(content().json(exceptedString));
    }

    @Test
    void getCurrentUser_notAuthenticated() throws Exception {
        mockMvc.perform(get("/users/current"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "some_user")
    void getCurrentUser_ok() throws Exception {
        var saved = usersRepository.save(User.builder()
                .login("some_user")
                .passwordHash(encoder.encode("password"))
                .userState(UserState.ACTIVE)
                .systemRoles(EnumSet.of(SystemRole.USER))
                .build()
        );
        final var excepted = UserDTO.builder()
                .id(saved.getId())
                .login(saved.getLogin())
                .fio(saved.getFio())
                .state(saved.getUserState())
                .systemRoles(saved.getSystemRoles())
                .build();

        final var exceptedString = objectMapper.writeValueAsString(excepted);

        mockMvc.perform(get("/users/current"))
                .andExpect(status().isOk())
                .andExpect(content().json(exceptedString));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createUser_ok() throws Exception {

        final var rq = CreateUserRequestDTO.builder()
                .login("some_user")
                .password("some_password")
                .systemRoles(EnumSet.of(SystemRole.USER))
                .fio("some fio")
                .picturePath("some/picture/path")
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.login").value(rq.getLogin()))
                .andExpect(jsonPath("$.fio").value(rq.getFio()))
                .andExpect(jsonPath("$.picturePath").value(rq.getPicturePath()))
                .andExpect(jsonPath("$.systemRoles", hasSize(1)))
                .andExpect(jsonPath("$.systemRoles[0]").value("USER"));

        assertCanAuth("some_user", "some_password");
    }

    @Test
    @WithMockUser(username = "root", roles = "SUPERUSER")
    void createAdmin_ok() throws Exception {

        final var rq = CreateUserRequestDTO.builder()
                .login("some_admin")
                .password("some_password")
                .systemRoles(EnumSet.of(SystemRole.ADMIN))
                .fio("some fio")
                .picturePath("some/picture/path")
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.login").value(rq.getLogin()))
                .andExpect(jsonPath("$.fio").value(rq.getFio()))
                .andExpect(jsonPath("$.picturePath").value(rq.getPicturePath()))
                .andExpect(jsonPath("$.systemRoles", hasSize(1)))
                .andExpect(jsonPath("$.systemRoles[0]").value("ADMIN"));

        assertCanAuth("some_admin", "some_password");
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createAdmin_hasNoPremission() throws Exception {

        final var rq = CreateUserRequestDTO.builder()
                .login("some_admin")
                .password("some_password")
                .systemRoles(EnumSet.of(SystemRole.ADMIN))
                .fio("some fio")
                .picturePath("some/picture/path")
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rq)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.detail").value("Only superuser can assign or edit administrator"));

        assertCantAuth("some_admin", "some_password");
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createSuperuser_hasNoPremission() throws Exception {

        final var rq = CreateUserRequestDTO.builder()
                .login("some_admin")
                .password("some_password")
                .systemRoles(EnumSet.of(SystemRole.SUPERUSER))
                .fio("some fio")
                .picturePath("some/picture/path")
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rq)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.detail").value("Assigning and editing superusers are forbidden"));

        assertCantAuth("some_admin", "some_password");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void changePassword_ok() throws Exception {
        var saved = usersRepository.save(User.builder()
                .login("login")
                .passwordHash(encoder.encode("password"))
                .userState(UserState.ACTIVE)
                .systemRoles(EnumSet.of(SystemRole.USER))
                .build()
        );
        final var excepted = UserDTO.builder()
                .id(saved.getId())
                .login(saved.getLogin())
                .fio(saved.getFio())
                .state(saved.getUserState())
                .systemRoles(saved.getSystemRoles())
                .build();

        final var exceptedString = objectMapper.writeValueAsString(excepted);

        mockMvc.perform(post("/users/login/changePassword")
                        .content("newPassword"))
                .andExpect(status().isOk())
                .andExpect(content().json(exceptedString));

        assertCanAuth("login", "newPassword");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUser_ok() throws Exception {
        var saved = usersRepository.save(User.builder()
                .login("login")
                .fio("some fio")
                .passwordHash(encoder.encode("password"))
                .userState(UserState.ACTIVE)
                .systemRoles(EnumSet.of(SystemRole.USER))
                .build()
        );

        final var rq = UpdateUserRequestDTO.builder()
                .login("new_login")
                .fio("new fio")
                .build();

        final var excepted = UserDTO.builder()
                .id(saved.getId())
                .login(rq.getLogin())
                .fio(rq.getFio())
                .state(saved.getUserState())
                .systemRoles(saved.getSystemRoles())
                .build();

        final var exceptedString = objectMapper.writeValueAsString(excepted);

        mockMvc.perform(put("/users/" + saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rq)))
                .andExpect(status().isOk())
                .andExpect(content().json(exceptedString));

        assertCanAuth("new_login", "password");
    }

    @Test
    @WithMockUser
    void searchByCriteria() throws Exception {
        usersRepository.saveAll(List.of(
                User.builder()
                        .login("login1")
                        .fio("some name")
                        .passwordHash(encoder.encode("password"))
                        .userState(UserState.ACTIVE)
                        .systemRoles(EnumSet.of(SystemRole.USER))
                        .build(),
                User.builder()
                        .login("login2")
                        .fio("some fio 2")
                        .passwordHash(encoder.encode("password"))
                        .userState(UserState.ACTIVE)
                        .systemRoles(EnumSet.of(SystemRole.USER, SystemRole.ADMIN))
                        .build(),

                User.builder()
                        .login("login3")
                        .fio("some fio 3")
                        .passwordHash(encoder.encode("password"))
                        .userState(UserState.BLOCKED)
                        .systemRoles(EnumSet.of(SystemRole.ADMIN))
                        .build()
        ));

        final var rq0 = UserFilterDTO.builder()
                .login(new StringFilter().setStartsWith("login"))
                .build();

        mockMvc.perform(post("/users/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rq0)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));

        final var rq1 = UserFilterDTO.builder()
                .fio(new StringFilter().setContains("fio"))
                .build();

        mockMvc.perform(post("/users/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rq1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        final var rq2 = UserFilterDTO.builder()
                .state(new Filter<UserState>().setEq(UserState.ACTIVE))
                .build();

        mockMvc.perform(post("/users/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rq2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        final var rq3 = UserFilterDTO.builder()
                .fio(new StringFilter().setContains("fio"))
                .state(new Filter<UserState>().setEq(UserState.ACTIVE))
                .build();

        mockMvc.perform(post("/users/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rq3)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }
}