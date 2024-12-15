package ru.dozen.mephi.meta.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import ru.dozen.mephi.meta.AbstractIntegrationTest;
import ru.dozen.mephi.meta.domain.User;
import ru.dozen.mephi.meta.domain.enums.UserState;
import ru.dozen.mephi.meta.web.model.UserDTO;

class UserControllerTest extends AbstractIntegrationTest {

    @Test
    void createAuthenticationToken_notAuthenticated() throws Exception {
        mockMvc.perform(get("/users/login"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void createAuthenticationToken_notFound() throws Exception {
        mockMvc.perform(get("/users/login"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void createAuthenticationToken_ok() throws Exception {
        var saved = usersRepository.save(User.builder()
                .login("login")
                .passwordHash(encoder.encode("password"))
                .userState(UserState.ACTIVE)
                .build()
        );
        final var excepted = UserDTO.builder()
                .id(saved.getId())
                .login(saved.getLogin())
                .fio(saved.getFio())
                .state(saved.getUserState())
                .build();

        final var exceptedString = objectMapper.writeValueAsString(excepted);

        mockMvc.perform(get("/users/login"))
                .andExpect(status().isOk())
                .andExpect(content().json(exceptedString));
    }
}