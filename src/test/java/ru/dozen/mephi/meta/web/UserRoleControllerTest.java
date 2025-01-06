package ru.dozen.mephi.meta.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import ru.dozen.mephi.meta.AbstractIntegrationTest;
import ru.dozen.mephi.meta.domain.UserRole;

@WithMockUser(roles = "ADMIN")
class UserRoleControllerTest extends AbstractIntegrationTest {

    @Test
    void getAllUserRoles() throws Exception {
        var list = List.of(
                new UserRole(null, "developer", "Разработчик"),
                new UserRole(null, "tester", "Тестировщик"),
                new UserRole(null, "analyzer", "Аналитик"));
        list = rolesRepository.saveAll(list);

        mockMvc.perform(get("/user-roles"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(list)));
    }

    @Test
    void createUserRole() throws Exception {
        assertEquals(0, rolesRepository.count());

        String rq = "{\"roleName\":\"ИмяРоли\", \"description\": \"Описание роли\"}";

        mockMvc.perform(post("/user-roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(rq))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.roleName").value("ИмяРоли"))
                .andExpect(jsonPath("$.description").value("Описание роли"));

        assertEquals(1, rolesRepository.count());
    }


    @Test
    @WithMockUser
    void createUserRole_forbidden() throws Exception {
        assertEquals(0, rolesRepository.count());

        String rq = "{\"roleName\":\"ИмяРоли\", \"description\": \"Описание роли\"}";

        mockMvc.perform(post("/user-roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(rq))
                .andExpect(status().isForbidden());

        assertEquals(0, rolesRepository.count());
    }

    @Test
    void updateUserRole() throws Exception {
        var role = rolesRepository.save(new UserRole(null, "devloper", "Разработчик"));
        assertEquals(1, rolesRepository.count());

        String rq = "{\"id\":" + role.getId() + ", \"roleName\":\"developer\"}";

        mockMvc.perform(put("/user-roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(rq))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(role.getId()))
                .andExpect(jsonPath("$.roleName").value("developer"))
                .andExpect(jsonPath("$.description").value("Разработчик"));

        assertEquals(1, rolesRepository.count());
    }

    @Test
    void deleteUserRole() throws Exception {
        var role = rolesRepository.save(new UserRole(null, "developer", "Разработчик"));
        assertEquals(1, rolesRepository.count());

        mockMvc.perform(delete("/user-roles/" + role.getId()))
                .andExpect(status().isNoContent());

        assertEquals(0, rolesRepository.count());
    }
}