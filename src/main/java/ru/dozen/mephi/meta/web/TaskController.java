package ru.dozen.mephi.meta.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.dozen.mephi.meta.service.TasksService;
import ru.dozen.mephi.meta.web.model.comment.CreateCommentRequestDTO;
import ru.dozen.mephi.meta.web.model.task.ChangeTaskStateRequestDTO;
import ru.dozen.mephi.meta.web.model.task.CreateTaskRequestDTO;
import ru.dozen.mephi.meta.web.model.task.TaskDTO;
import ru.dozen.mephi.meta.web.model.task.TaskFilterDTO;
import ru.dozen.mephi.meta.web.model.task.TaskShortInfoDTO;
import ru.dozen.mephi.meta.web.model.task.UpdateTaskRequestDTO;
import ru.dozen.mephi.meta.web.model.user.UserDTO;

@RestController
@RequiredArgsConstructor
@RequestMapping("/projects/{projectId}/tasks")
@SecurityRequirement(name = "bearer-auth")
@Tag(name = "Управление задачами")
public class TaskController {

    private final TasksService tasksService;

    private static final String CHECK_HAS_USER_RIGHTS = "T(ru.dozen.mephi.meta.util.AuthoritiesUtils).isMemberOfProjectAndHasAnyRole(principal, #projectId, 'ROLE_USER')";
    private static final String CHECK_HAS_USER_OR_SERVICE_RIGHTS = "T(ru.dozen.mephi.meta.util.AuthoritiesUtils).isMemberOfProjectAndHasAnyRole(principal, #projectId, 'ROLE_USER', 'ROLE_SERVICE')";

    @Operation(
            description = "Получение информации о задаче по её проекту и ключу",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Запрос выполнен"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Не найдена задача"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Не авторизован"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Нет прав"
                    )
            }
    )
    @GetMapping("/{key}")
    @PreAuthorize(CHECK_HAS_USER_RIGHTS)
    public ResponseEntity<TaskDTO> getTaskByKey(@PathVariable long projectId, @PathVariable String key) {
        return ResponseEntity.ok(tasksService.getTask(projectId, key));
    }

    @Operation(
            description = "Создание задачи в проекте",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Запрос выполнен"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Не найден проект"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Не авторизован"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Нет прав"
                    )
            }
    )
    @PostMapping
    @PreAuthorize(CHECK_HAS_USER_RIGHTS)
    public ResponseEntity<TaskDTO> createTask(@PathVariable long projectId, @RequestBody CreateTaskRequestDTO rq) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tasksService.createTask(projectId, rq));
    }

    @Operation(
            description = "Поиск задач по критериям",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Запрос выполнен"),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Не авторизован"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Нет прав"
                    )
            }
    )
    @PostMapping("/search")
    @PreAuthorize(CHECK_HAS_USER_RIGHTS)
    public ResponseEntity<List<TaskDTO>> searchTasks(@PathVariable long projectId, @RequestBody TaskFilterDTO rq) {
        return ResponseEntity.ok(tasksService.searchTasks(projectId, rq));
    }

    @Operation(
            description = "Поиск кратких описаний задач по критериям",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Запрос выполнен"),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Не авторизован"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Нет прав"
                    )
            }
    )
    @PostMapping("/search-shorten")
    @PreAuthorize(CHECK_HAS_USER_OR_SERVICE_RIGHTS)
    public ResponseEntity<List<TaskShortInfoDTO>> searchTasksShortInfo(
            @PathVariable long projectId,
            @RequestBody TaskFilterDTO rq
    ) {
        return ResponseEntity.ok(tasksService.searchTasksShortInfo(projectId, rq));
    }

    @Operation(
            description = "Изменение задачи",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Запрос выполнен"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Не найдена задача"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Не авторизован"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Нет прав"
                    )
            }
    )
    @PutMapping("/{key}")
    @PreAuthorize(CHECK_HAS_USER_RIGHTS)
    public ResponseEntity<TaskDTO> updateTask(
            @PathVariable long projectId,
            @PathVariable String key,
            @RequestBody UpdateTaskRequestDTO rq
    ) {
        return ResponseEntity.ok(tasksService.updateTask(projectId, key, rq));
    }

    @Operation(
            description = "Изменение состояния задачи",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Запрос выполнен"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Не найдена задача"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Не авторизован"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Нет прав"
                    )
            }
    )
    @PutMapping("/{key}/state")
    @PreAuthorize(CHECK_HAS_USER_RIGHTS)
    public ResponseEntity<TaskDTO> changeTaskState(
            @PathVariable long projectId,
            @PathVariable String key,
            @RequestBody ChangeTaskStateRequestDTO rq
    ) {
        return ResponseEntity.ok(tasksService.changeTaskState(projectId, key, rq));
    }

    @Operation(
            description = "Добавить комментарий к задаче от имени текущего пользователя",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Запрос выполнен"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Не найдена задача"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Не авторизован"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Нет прав"
                    )
            }
    )
    @PostMapping("/{key}/comment")
    @PreAuthorize(CHECK_HAS_USER_RIGHTS)
    public ResponseEntity<TaskDTO> addComment(
            @PathVariable long projectId,
            @PathVariable String key,
            @RequestBody CreateCommentRequestDTO rq
    ) {
        return ResponseEntity.ok(tasksService.addComment(projectId, key, rq));
    }

    @Operation(
            description = "Добавление наблюдателя в задачу",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Запрос выполнен"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Не найдена задача или пользователь пользователь"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Не авторизован"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Нет прав"
                    )
            }
    )
    @PostMapping("/{key}/watcher")
    @PreAuthorize(CHECK_HAS_USER_RIGHTS)
    public ResponseEntity<TaskDTO> addWatcher(
            @PathVariable long projectId,
            @PathVariable String key,
            @RequestBody UserDTO watcher
    ) {
        return ResponseEntity.ok(tasksService.addWatcher(projectId, key, watcher));
    }

    @Operation(
            description = "Удаление наблюдателя из задачи",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Запрос выполнен"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Не найдена задача"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Не авторизован"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Нет прав"
                    )
            }
    )
    @DeleteMapping("/{key}/watcher")
    @PreAuthorize(CHECK_HAS_USER_RIGHTS)
    public ResponseEntity<TaskDTO> removeWatcher(
            @PathVariable long projectId,
            @PathVariable String key,
            @RequestBody UserDTO watcher
    ) {
        return ResponseEntity.ok(tasksService.removeWatcher(projectId, key, watcher));
    }
}
