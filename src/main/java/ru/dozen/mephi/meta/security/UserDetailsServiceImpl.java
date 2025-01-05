package ru.dozen.mephi.meta.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dozen.mephi.meta.domain.User;
import ru.dozen.mephi.meta.repository.UsersRepository;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsersRepository usersRepository;

    @Override
    @Transactional
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        var userOptional = usersRepository.findByLogin(username);
        return userOptional.orElseThrow(() -> new UsernameNotFoundException(username));
    }
}
