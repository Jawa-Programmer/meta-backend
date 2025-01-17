package ru.dozen.mephi.meta.web;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.github.tomakehurst.wiremock.client.WireMock;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.http.MediaType;
import ru.dozen.mephi.meta.AbstractIntegrationTest;
import ru.dozen.mephi.meta.client.model.TestStatus;
import ru.dozen.mephi.meta.client.model.TestStatusResponseDTO;
import ru.dozen.mephi.meta.client.model.TestType;
import ru.dozen.mephi.meta.domain.Project;
import ru.dozen.mephi.meta.domain.RoleRecord;
import ru.dozen.mephi.meta.domain.Task;
import ru.dozen.mephi.meta.domain.User;
import ru.dozen.mephi.meta.domain.UserRole;
import ru.dozen.mephi.meta.domain.enums.ProjectState;
import ru.dozen.mephi.meta.domain.enums.TaskState;
import ru.dozen.mephi.meta.util.filter.StringFilter;
import ru.dozen.mephi.meta.web.model.comment.CreateCommentRequestDTO;
import ru.dozen.mephi.meta.web.model.task.ChangeTaskStateRequestDTO;
import ru.dozen.mephi.meta.web.model.task.CreateTaskRequestDTO;
import ru.dozen.mephi.meta.web.model.task.TaskFilterDTO;
import ru.dozen.mephi.meta.web.model.task.UpdateTaskRequestDTO;
import ru.dozen.mephi.meta.web.model.user.UserDTO;

class TaskControllerTest extends AbstractIntegrationTest {

    private Long MOCK_PROJECT_ID;
    private Long MOCK_TASK_ID;

    @BeforeEach
    void setUpBaseMocks() {
        var roles = List.of(
                new UserRole(null, "ROLE_1", "Role 1"),
                new UserRole(null, "ROLE_2", "Role 2")
        );
        roles = rolesRepository.saveAll(roles);

        var admin = usersRepository.save(readResourceValue("mocks/user/MockAdmin.json", User.class));
        var service = usersRepository.save(readResourceValue("mocks/user/MockService.json", User.class));
        var user1 = usersRepository.save(readResourceValue("mocks/user/MockUser1.json", User.class));
        var user2 = usersRepository.save(readResourceValue("mocks/user/MockUser2.json", User.class));
        var user3 = usersRepository.save(readResourceValue("mocks/user/MockUser3.json", User.class));

        var project = new Project(null, "Mock project", ProjectState.ACTIVE, user1, new ArrayList<>(),
                new ArrayList<>());
        project = projectsRepository.save(project);

        var roleRecords = List.of(
                new RoleRecord(null, roles.get(0), user1, project),
                new RoleRecord(null, roles.get(1), user2, project)
        );
        project.getRoleRecords().addAll(roleRecords);
        project = projectsRepository.save(project);

        var task = new Task(null, "TSK-1", "Title", "Description", true,
                user1, user2, project, TaskState.NEW, new ArrayList<>(), new ArrayList<>());

        MOCK_TASK_ID = tasksRepository.save(task).getId();

        MOCK_PROJECT_ID = project.getId();
    }

    @ParameterizedTest
    @EnumSource(TestStatus.class)
    void getTaskByKey(TestStatus status) throws Exception {
        var testStatus = TestStatusResponseDTO.builder()
                .taskId(MOCK_TASK_ID)
                .testType(TestType.TEST_CASE)
                .status(status)
                .build();

        stubFor(WireMock.get(urlEqualTo("/public/tasks/" + MOCK_TASK_ID + "/status"))
                .withHeader("Authorization", WireMock.equalTo("Bearer some-token"))
                .willReturn(
                        aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody(objectMapper.writeValueAsString(testStatus))
                )
        );

        mockMvc.perform(get("/projects/" + MOCK_PROJECT_ID + "/tasks/TSK-1")
                        .headers(auth("user1", "password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Title"))
                .andExpect(jsonPath("$.testStatus").value(status.getDescription()));
    }

    @Test
    void getTaskByKey_externalError() throws Exception {
        stubFor(WireMock.get(urlEqualTo("/public/tasks/" + MOCK_TASK_ID + "/status"))
                .withHeader("Authorization", WireMock.equalTo("Bearer some-token"))
                .willReturn(aResponse().withStatus(404)));

        mockMvc.perform(get("/projects/" + MOCK_PROJECT_ID + "/tasks/TSK-1")
                        .headers(auth("user1", "password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Title"))
                .andExpect(jsonPath("$.testStatus").isEmpty());
    }

    @Test
    void getTaskByKey_notInProject() throws Exception {
        mockMvc.perform(get("/projects/" + MOCK_PROJECT_ID + "/tasks/TSK-1")
                        .headers(auth("user3", "password")))
                .andExpect(status().isForbidden());
    }

    @Test
    void createTask() throws Exception {

        var rq1 = CreateTaskRequestDTO.builder()
                .title("New Task")
                .key("TSK-42")
                .description("New Description")
                .executor(UserDTO.builder().login("user2").build())
                .build();
        mockMvc.perform(post("/projects/" + MOCK_PROJECT_ID + "/tasks")
                        .headers(auth("user1", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(rq1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.key").value("TSK-42"))
                .andExpect(jsonPath("$.title").value("New Task"))
                .andExpect(jsonPath("$.description").value("New Description"))
                .andExpect(jsonPath("$.author.login").value("user1"))
                .andExpect(jsonPath("$.executor.login").value("user2"))
                .andExpect(jsonPath("$.taskState").value("NEW"));

        var rq2 = CreateTaskRequestDTO.builder()
                .title("New Task")
                .description("New Description")
                .executor(UserDTO.builder().login("user2").build())
                .build();
        mockMvc.perform(post("/projects/" + MOCK_PROJECT_ID + "/tasks")
                        .headers(auth("user1", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(rq2)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.key").value("TSK-43"))
                .andExpect(jsonPath("$.title").value("New Task"))
                .andExpect(jsonPath("$.description").value("New Description"))
                .andExpect(jsonPath("$.author.login").value("user1"))
                .andExpect(jsonPath("$.executor.login").value("user2"))
                .andExpect(jsonPath("$.taskState").value("NEW"));
    }

    @Test
    void searchTasks() throws Exception {
        var rq1 = TaskFilterDTO.builder()
                .authorLogin(new StringFilter().setContains("use"))
                .build();
        mockMvc.perform(post("/projects/" + MOCK_PROJECT_ID + "/tasks/search")
                        .headers(auth("user1", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(rq1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        var rq2 = TaskFilterDTO.builder()
                .executorFio((StringFilter) new StringFilter().setIsNull(false))
                .build();
        mockMvc.perform(post("/projects/" + MOCK_PROJECT_ID + "/tasks/search")
                        .headers(auth("user1", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(rq2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", empty()));
    }

    @Test
    void searchTasksShortInfo() throws Exception {
        var rq1 = TaskFilterDTO.builder()
                .authorLogin(new StringFilter().setContains("use"))
                .build();
        mockMvc.perform(post("/projects/" + MOCK_PROJECT_ID + "/tasks/search-shorten")
                        .headers(auth("user1", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(rq1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void updateTask() throws Exception {
        var rq1 = UpdateTaskRequestDTO.builder()
                .description("New Description")
                .build();

        mockMvc.perform(put("/projects/" + MOCK_PROJECT_ID + "/tasks/TSK-1")
                        .headers(auth("user1", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(rq1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.key").value("TSK-1"))
                .andExpect(jsonPath("$.description").value("New Description"));

    }

    @Test
    void changeTaskState() throws Exception {
        var rq1 = ChangeTaskStateRequestDTO.builder()
                .state(TaskState.ASSIGNED)
                .build();

        mockMvc.perform(put("/projects/" + MOCK_PROJECT_ID + "/tasks/TSK-1/state")
                        .headers(auth("user1", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(rq1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.key").value("TSK-1"))
                .andExpect(jsonPath("$.taskState").value("ASSIGNED"));
    }

    @Test
    void addComment() throws Exception {
        var rq1 = CreateCommentRequestDTO.builder()
                .text("Чувак, твой код - говно")
                .build();

        mockMvc.perform(post("/projects/" + MOCK_PROJECT_ID + "/tasks/TSK-1/comment")
                        .headers(auth("user1", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(rq1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.key").value("TSK-1"))
                .andExpect(jsonPath("$.comments[0].author.login").value("user1"))
                .andExpect(jsonPath("$.comments[0].text").value("Чувак, твой код - говно"));
    }

    @Test
    void watcher() throws Exception {
        var rq1 = UserDTO.builder()
                .login("user2")
                .build();

        mockMvc.perform(post("/projects/" + MOCK_PROJECT_ID + "/tasks/TSK-1/watcher")
                        .headers(auth("user1", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(rq1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.key").value("TSK-1"))
                .andExpect(jsonPath("$.watchers[0].login").value("user2"));

        mockMvc.perform(delete("/projects/" + MOCK_PROJECT_ID + "/tasks/TSK-1/watcher")
                        .headers(auth("user1", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(rq1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.key").value("TSK-1"))
                .andExpect(jsonPath("$.watchers", empty()));
    }

}