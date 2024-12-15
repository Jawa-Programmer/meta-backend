package ru.dozen.mephi.meta.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.dozen.mephi.meta.domain.Project;

@Repository
public interface ProjectsRepository extends JpaRepository<Project, Long> {

    Optional<Project> findByTitle(String title);

    List<Project> findByTitleContains(String title);

}
