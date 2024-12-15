package ru.dozen.mephi.meta.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.dozen.mephi.meta.service.UsersService;
import ru.dozen.mephi.meta.web.model.UserDTO;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UsersService usersService;

    @GetMapping("/{login}")
    public ResponseEntity<UserDTO> createAuthenticationToken(@PathVariable String login) {
        return ResponseEntity.ok(usersService.getUserByLogin(login));
    }
}
