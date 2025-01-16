package ru.dozen.mephi.meta.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.dozen.mephi.meta.domain.Task;

@Repository
public interface TasksRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {

    @Query(value = "SELECT * FROM tasks WHERE project_id = :projectId AND key = :key", nativeQuery = true)
    Optional<Task> findByProjectIdAndKey(long projectId, String key);

    @Query(value = "SELECT try_cast_int(SUBSTRING(key, 5), -1) as kvalue FROM tasks WHERE key LIKE 'TSK-%' ORDER BY kvalue DESC LIMIT 1", nativeQuery = true)
    Optional<Integer> getMaxKey();

    boolean existsByKey(String key);
}
