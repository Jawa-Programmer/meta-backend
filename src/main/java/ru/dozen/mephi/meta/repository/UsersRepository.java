package ru.dozen.mephi.meta.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.dozen.mephi.meta.domain.User;

@Repository
public interface UsersRepository extends JpaRepository<User, Long> {

    Optional<User> findByLogin(String login);

    List<User> findAllByLoginContains(String login);

    List<User> findAllByFioContains(String fio);
}
