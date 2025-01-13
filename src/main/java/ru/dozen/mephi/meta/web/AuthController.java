package ru.dozen.mephi.meta.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.dozen.mephi.meta.security.AuthRequest;
import ru.dozen.mephi.meta.security.JwtTokenUtil;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;

    private final JwtTokenUtil jwtTokenUtil;


    @PostMapping
    public ResponseEntity<String> createAuthenticationToken(@RequestBody AuthRequest authRequest) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        );

        var userDetails = (UserDetails) auth.getPrincipal();
        var result = jwtTokenUtil.generateToken(userDetails.getUsername());
        return ResponseEntity.ok(result);
    }
}