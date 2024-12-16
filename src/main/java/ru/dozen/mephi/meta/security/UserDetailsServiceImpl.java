package ru.dozen.mephi.meta.security;

import java.util.EnumSet;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.dozen.mephi.meta.domain.enums.SystemRole;
import ru.dozen.mephi.meta.domain.enums.UserState;
import ru.dozen.mephi.meta.repository.UsersRepository;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsersRepository usersRepository;

    private static String[] toStrings(EnumSet<SystemRole> roles) {
        return roles.stream().map(SystemRole::toString).toArray(String[]::new);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var userOptional = usersRepository.findByLogin(username);
        return userOptional.map(user -> User.withUsername(user.getLogin())
                .password(user.getPasswordHash())
                .disabled(!UserState.ACTIVE.equals(user.getUserState()))
                .roles(toStrings(user.getSystemRoles()))
                .build()
        ).orElseThrow(() -> new UsernameNotFoundException(username));
    }
}
