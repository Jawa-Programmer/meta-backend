package ru.dozen.mephi.meta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.dozen.mephi.meta.domain.UserRole;

@Repository
public interface UserRolesRepository extends JpaRepository<UserRole, Long> {

}
