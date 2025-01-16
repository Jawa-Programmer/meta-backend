package ru.dozen.mephi.meta.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
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
import ru.dozen.mephi.meta.service.ProjectService;
import ru.dozen.mephi.meta.web.model.project.AssignRemoveParticipantRequestDTO;
import ru.dozen.mephi.meta.web.model.project.ChangeProjectStateRequestDTO;
import ru.dozen.mephi.meta.web.model.project.CreateProjectRequestDTO;
import ru.dozen.mephi.meta.web.model.project.ParticipantsDTO;
import ru.dozen.mephi.meta.web.model.project.ProjectDTO;
import ru.dozen.mephi.meta.web.model.project.ProjectFilterDTO;
import ru.dozen.mephi.meta.web.model.project.UpdateRoleRequestDTO;

@RestController
@RequiredArgsConstructor
@RequestMapping("/projects")
@SecurityRequirement(name = "bearer-auth")
@Tag(name = "Управление проектами")
public class ProjectController {

    private final ProjectService projectService;

    private static final String CHECK_IS_PROJECT_DIRECTOR = "T(ru.dozen.mephi.meta.util.AuthoritiesUtils).isDirectorOfProject(principal, #projectId)";
    private static final String CHECK_IS_MEMBER_OR_DIRECTOR = "T(ru.dozen.mephi.meta.util.AuthoritiesUtils).isMemberOfProject(principal, #projectId)";

    @Operation(
            description = "Получение информации о проекте по ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Запрос выполнен"),
                    @ApiResponse(responseCode = "404", description = "Проект не найден"),
                    @ApiResponse(responseCode = "401", description = "Не авторизован")
            }
    )
    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectDTO> getProjectById(@PathVariable Long projectId) {
        return ResponseEntity.ok(projectService.getProject(projectId));
    }

    @Operation(
            description = "Создание нового проекта",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Проект создан"),
                    @ApiResponse(responseCode = "401", description = "Не авторизован"),
                    @ApiResponse(responseCode = "403", description = "Нет прав")
            }
    )
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERUSER')")
    public ResponseEntity<ProjectDTO> createProject(@Valid @RequestBody CreateProjectRequestDTO rq) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.createProject(rq));

    }

    @Operation(
            description = "Изменение состояния проекта",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Запрос выполнен"),
                    @ApiResponse(responseCode = "404", description = "Проект не найден"),
                    @ApiResponse(responseCode = "401", description = "Не авторизован"),
                    @ApiResponse(responseCode = "403", description = "Нет прав")
            }
    )
    @PutMapping("/{projectId}/state")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERUSER')")
    public ResponseEntity<ProjectDTO> changeProjectState(
            @PathVariable long projectId,
            @RequestBody @Valid ChangeProjectStateRequestDTO request
    ) {
        return ResponseEntity.ok(projectService.changeProjectState(projectId, request));
    }

    @Operation(
            description = "Изменение информации о проекте",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Запрос выполнен"),
                    @ApiResponse(responseCode = "401", description = "Не авторизован"),
                    @ApiResponse(responseCode = "403", description = "Нет прав")
            }
    )
    @PutMapping("/{projectId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERUSER')")
    public ResponseEntity<ProjectDTO> updateProject(@PathVariable Long projectId, @Valid @RequestBody CreateProjectRequestDTO request) {
        return ResponseEntity.ok(projectService.updateProject(projectId, request));
    }

    @Operation(
            description = "Получение списка участников проекта с их ролями",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Запрос выполнен"),
                    @ApiResponse(responseCode = "404", description = "Проект не найден"),
                    @ApiResponse(responseCode = "401", description = "Не авторизован"),
                    @ApiResponse(responseCode = "403", description = "Нет прав")
            }
    )
    @GetMapping("/{projectId}/participants")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERUSER') || " + CHECK_IS_PROJECT_DIRECTOR)
    public ResponseEntity<List<ParticipantsDTO>> getParticipants(@PathVariable long projectId) {
        return ResponseEntity.ok(projectService.getParticipants(projectId));
    }

    @Operation(
            description = "Назначение участника в проект",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Участник успешно назначен"),
                    @ApiResponse(responseCode = "404", description = "Проект или пользователь не найден"),
                    @ApiResponse(responseCode = "401", description = "Не авторизован"),
                    @ApiResponse(responseCode = "403", description = "Нет прав")
            }
    )
    @PostMapping("/{projectId}/participants")
    @PreAuthorize(CHECK_IS_PROJECT_DIRECTOR)
    public ResponseEntity<ProjectDTO> assignParticipant(
            @PathVariable long projectId,
            @RequestBody @Valid AssignRemoveParticipantRequestDTO request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.assignParticipant(projectId, request));
    }

    @Operation(
            description = "Удаление участника из проекта",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Участник успешно удалён"),
                    @ApiResponse(responseCode = "404", description = "Проект, пользователь или роль не найдены"),
                    @ApiResponse(responseCode = "401", description = "Не авторизован"),
                    @ApiResponse(responseCode = "403", description = "Нет прав")
            }
    )
    @DeleteMapping("/{projectId}/participants")
    @PreAuthorize(CHECK_IS_PROJECT_DIRECTOR)
    public ResponseEntity<List<ParticipantsDTO>> removeParticipant(
            @PathVariable long projectId,
            @RequestBody @Valid AssignRemoveParticipantRequestDTO request
    ) {
        return ResponseEntity.ok(projectService.removeParticipant(projectId, request));
    }

    @Operation(
            description = "Обновление роли участника в проекте",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Роль обновлена успешно"),
                    @ApiResponse(responseCode = "404", description = "Участник, роль или проект не найдены"),
                    @ApiResponse(responseCode = "401", description = "Не авторизован"),
                    @ApiResponse(responseCode = "403", description = "Нет прав")
            }
    )
    @PutMapping("/{projectId}/participants/role")
    @PreAuthorize(CHECK_IS_PROJECT_DIRECTOR)
    public ResponseEntity<ParticipantsDTO> updateParticipantRole(
            @PathVariable long projectId,
            @RequestBody @Valid UpdateRoleRequestDTO request
    ) {
        return ResponseEntity.ok(projectService.updateParticipantRole(projectId, request));
    }

    @Operation(
            summary = "Поиск проектов по фильтру",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Запрос выполнен"),
                    @ApiResponse(responseCode = "401", description = "Не авторизован"),
                    @ApiResponse(responseCode = "403", description = "Нет прав")
            }
    )
    @GetMapping("/search")
    public ResponseEntity<List<ProjectDTO>> searchProjects(@Valid @ParameterObject ProjectFilterDTO filter) {
        return ResponseEntity.ok(projectService.searchProjects(filter));
    }
}
