package ru.dozen.mephi.meta.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.EnumSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import ru.dozen.mephi.meta.AbstractIntegrationTest;
import ru.dozen.mephi.meta.domain.User;
import ru.dozen.mephi.meta.domain.enums.SystemRole;
import ru.dozen.mephi.meta.domain.enums.UserState;
import ru.dozen.mephi.meta.security.AuthRequest;
import ru.dozen.mephi.meta.security.JwtTokenUtil;

class AuthControllerTest extends AbstractIntegrationTest {


    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @BeforeEach
    void initDatabase() {
        usersRepository.save(User.builder()
                .login("user")
                .passwordHash(encoder.encode("password"))
                .userState(UserState.ACTIVE)
                .systemRoles(EnumSet.of(SystemRole.USER))
                .build()
        );
    }

    @Test
    void testAuthenticate() throws Exception {
        AuthRequest authRequest = AuthRequest.builder()
                .username("user")
                .password("password")
                .build();

        var data = mockMvc.perform(post("/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        assertEquals("user", jwtTokenUtil.extractUsername(data));

        mockMvc.perform(get("/users/user"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/users/user").header("Authorization", "Bearer " + data))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "*"));
    }

    @Test
    void testAuthenticate_InvalidUser() throws Exception {
        AuthRequest authRequest = AuthRequest.builder()
                .username("ivan")
                .password("password")
                .build();

        mockMvc.perform(post("/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testAuthenticate_InvalidPass() throws Exception {
        AuthRequest authRequest = AuthRequest.builder()
                .username("user")
                .password("qwerty123")
                .build();

        mockMvc.perform(post("/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isUnauthorized());
    }
}