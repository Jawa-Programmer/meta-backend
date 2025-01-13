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
import ru.dozen.mephi.meta.service.UserRolesService;
import ru.dozen.mephi.meta.util.ProblemUtils;
import ru.dozen.mephi.meta.web.model.role.UserRoleDTO;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user-roles")
@SecurityRequirement(name = "bearer-auth")
@Tag(name = "Управление ролями пользователей")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPERUSER')")
public class UserRoleController {

    private final UserRolesService userRolesService;

    @Operation(
            description = "Получение списка всех ролей",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Запрос выполнен"),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Не авторизован"
                    )
            }
    )
    @GetMapping
    @PreAuthorize("true")
    public ResponseEntity<List<UserRoleDTO>> getAllUserRoles() {
        return ResponseEntity.ok(userRolesService.getAllUserRoles());
    }

    @Operation(
            description = "Создание новой роли",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Запрос выполнен"),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Ошибка формата запроса"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Не авторизован"
                    )
            }
    )
    @PostMapping
    public ResponseEntity<UserRoleDTO> createUserRole(@RequestBody UserRoleDTO userRoleDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userRolesService.createUserRole(userRoleDTO));
    }

    @Operation(
            description = "Обновление роли",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Запрос выполнен"),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Ошибка формата запроса"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Не авторизован"
                    )
            }
    )
    @PutMapping
    public ResponseEntity<UserRoleDTO> updateUserRole(@RequestBody UserRoleDTO userRoleDTO) {
        if (userRoleDTO.getId() == null) {
            throw ProblemUtils.badRequest("Id is required");
        }
        return ResponseEntity.ok(userRolesService.updateUserRole(userRoleDTO));
    }

    @Operation(
            description = "Удаление роли",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Запрос выполнен"),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Хотя бы один пользователь исполнят данную роль"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Не авторизован"
                    )
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<UserRoleDTO> deleteUserRole(@PathVariable Long id) {
        userRolesService.deleteUserRole(id);
        return ResponseEntity.noContent().build();
    }
}
