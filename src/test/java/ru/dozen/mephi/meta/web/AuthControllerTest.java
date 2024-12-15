package ru.dozen.mephi.meta.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import ru.dozen.mephi.meta.AbstractIntegrationTest;
import ru.dozen.mephi.meta.security.AuthRequest;

class AuthControllerTest extends AbstractIntegrationTest {


    @Test
    void testAuthenticate() throws Exception {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername("user");
        authRequest.setPassword("password");

        var res = mockMvc.perform(post("/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        System.out.println(res);
    }
}