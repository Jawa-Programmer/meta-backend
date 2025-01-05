package ru.dozen.mephi.meta.web;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.dozen.mephi.meta.AbstractIntegrationTest;
import ru.dozen.mephi.meta.client.AutomatedTestManagementSystemClient;
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

    @MockitoBean
    private AutomatedTestManagementSystemClient atmsClient;

    private Long MOCK_PROJECT_ID;

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

        tasksRepository.save(task);

        MOCK_PROJECT_ID = project.getId();
    }

    @Test
    void getTaskByKey() throws Exception {
        mockMvc.perform(get("/project/" + MOCK_PROJECT_ID + "/tasks/TSK-1")
                        .headers(auth("user1", "password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Title"));
    }

    @Test
    void getTaskByKey_notInProject() throws Exception {
        mockMvc.perform(get("/project/" + MOCK_PROJECT_ID + "/tasks/TSK-1")
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
        mockMvc.perform(post("/project/" + MOCK_PROJECT_ID + "/tasks")
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
        mockMvc.perform(post("/project/" + MOCK_PROJECT_ID + "/tasks")
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
        mockMvc.perform(post("/project/" + MOCK_PROJECT_ID + "/tasks/search")
                        .headers(auth("user1", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(rq1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        var rq2 = TaskFilterDTO.builder()
                .executorFio((StringFilter) new StringFilter().setIsNull(false))
                .build();
        mockMvc.perform(post("/project/" + MOCK_PROJECT_ID + "/tasks/search")
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
        mockMvc.perform(post("/project/" + MOCK_PROJECT_ID + "/tasks/search-shorten")
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

        mockMvc.perform(put("/project/" + MOCK_PROJECT_ID + "/tasks/TSK-1")
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

        mockMvc.perform(put("/project/" + MOCK_PROJECT_ID + "/tasks/TSK-1/state")
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

        mockMvc.perform(post("/project/" + MOCK_PROJECT_ID + "/tasks/TSK-1/comment")
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

        mockMvc.perform(post("/project/" + MOCK_PROJECT_ID + "/tasks/TSK-1/watcher")
                        .headers(auth("user1", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(rq1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.key").value("TSK-1"))
                .andExpect(jsonPath("$.watchers[0].login").value("user2"));

        mockMvc.perform(delete("/project/" + MOCK_PROJECT_ID + "/tasks/TSK-1/watcher")
                        .headers(auth("user1", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(rq1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.key").value("TSK-1"))
                .andExpect(jsonPath("$.watchers", empty()));
    }

}