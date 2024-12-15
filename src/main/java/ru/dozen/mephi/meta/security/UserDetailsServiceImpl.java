package ru.dozen.mephi.meta.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.dozen.mephi.meta.domain.enums.UserState;
import ru.dozen.mephi.meta.repository.UsersRepository;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsersRepository usersRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var userOptional = usersRepository.findByLogin(username);
        return userOptional.map(user -> User.withUsername(user.getLogin())
                .password(user.getPasswordHash())
                .disabled(!UserState.ACTIVE.equals(user.getUserState()))
                .roles("USER")
                .build()
        ).orElseThrow(() -> new UsernameNotFoundException(username));
    }
}
