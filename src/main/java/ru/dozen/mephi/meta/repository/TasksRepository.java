package ru.dozen.mephi.meta.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.dozen.mephi.meta.domain.Task;
import ru.dozen.mephi.meta.domain.User;

@Repository
public interface TasksRepository extends JpaRepository<Task, Long> {

    List<Task> findAllByKey(String key);

    List<Task> findAllByKeyContains(String key);

    @Query(value = "SELECT * FROM tasks WHERE author_id = :authorId", nativeQuery = true)
    List<Task> findAllByAuthorId(String authorId);

    List<Task> findAllByAuthor(User author);

    @Query(value = "SELECT * FROM tasks WHERE executor_id = :executorId", nativeQuery = true)
    List<Task> findAllByExecutorId(String executorId);

    List<Task> findAllByExecutor(User executor);


}
