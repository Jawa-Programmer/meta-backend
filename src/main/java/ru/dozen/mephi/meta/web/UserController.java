package ru.dozen.mephi.meta.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.dozen.mephi.meta.service.UsersService;
import ru.dozen.mephi.meta.web.model.user.CreateUserRequestDTO;
import ru.dozen.mephi.meta.web.model.user.UpdateUserRequestDTO;
import ru.dozen.mephi.meta.web.model.user.UserDTO;
import ru.dozen.mephi.meta.web.model.user.UserFilterDTO;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@SecurityRequirement(name = "swagger-auth")
@Tag(name = "Управление пользователями")
public class UserController {

    private final UsersService usersService;

    @Operation(
            description = "Получение информации о пользователе по его логину",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Запрос выполнен"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Не найден пользователь"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Не авторизован"
                    )
            }
    )
    @GetMapping("/{login}")
    public ResponseEntity<UserDTO> getUserByLogin(@PathVariable String login) {
        return ResponseEntity.ok(usersService.getUserByLogin(login));
    }

    @Operation(
            description = "Получение информации о текущем пользователе",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Запрос выполнен"),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Не авторизован"
                    )
            }
    )
    @GetMapping("/current")
    public ResponseEntity<UserDTO> getCurrentUser() {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(usersService.getUserByLogin(login));
    }

    @Operation(
            description = "Поиск пользователя по критерию",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Запрос выполнен"),
                    @ApiResponse(responseCode = "401", description = "Не авторизован"),
                    @ApiResponse(responseCode = "403", description = "Нет прав")
            }
    )
    @PostMapping("/search")
    public ResponseEntity<List<UserDTO>> searchUser(@RequestBody UserFilterDTO rq) {
        return ResponseEntity.ok(usersService.searchByFilter(rq));
    }

    @Operation(
            description = "Создание нового пользователя",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Запрос выполнен"),
                    @ApiResponse(responseCode = "401", description = "Не авторизован"),
                    @ApiResponse(responseCode = "403", description = "Нет прав")
            }
    )
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERUSER')")
    public ResponseEntity<UserDTO> createUser(@RequestBody CreateUserRequestDTO rq) {
        return ResponseEntity.ok(usersService.registerNewUser(rq));
    }


    @Operation(
            description = "Изменение пользователя",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Запрос выполнен"),
                    @ApiResponse(responseCode = "401", description = "Не авторизован"),
                    @ApiResponse(responseCode = "403", description = "Нет прав")
            }
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERUSER')")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UpdateUserRequestDTO rq) {
        return ResponseEntity.ok(usersService.updateUser(id, rq));
    }

    @Operation(
            description = "Установить новый пароль пользователю",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Запрос выполнен"),
                    @ApiResponse(responseCode = "401", description = "Не авторизован"),
                    @ApiResponse(responseCode = "403", description = "Нет прав")
            }
    )
    @PostMapping("/{login}/changePassword")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERUSER')")
    public ResponseEntity<UserDTO> changePassword(@PathVariable String login, @RequestBody @NotBlank String password) {
        return ResponseEntity.ok(usersService.setPassword(login, password));
    }
}
