package ru.dozen.mephi.meta.service.impl;

import static ru.dozen.mephi.meta.util.ProblemUtils.badRequest;
import static ru.dozen.mephi.meta.util.ProblemUtils.forbidden;
import static ru.dozen.mephi.meta.util.ProblemUtils.notFound;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.zalando.problem.ThrowableProblem;
import ru.dozen.mephi.meta.client.AutomatedTestManagementSystemClient;
import ru.dozen.mephi.meta.domain.Comment;
import ru.dozen.mephi.meta.domain.Task;
import ru.dozen.mephi.meta.domain.User;
import ru.dozen.mephi.meta.repository.TasksRepository;
import ru.dozen.mephi.meta.repository.UsersRepository;
import ru.dozen.mephi.meta.service.TasksService;
import ru.dozen.mephi.meta.service.mapper.TaskMapper;
import ru.dozen.mephi.meta.util.AuthoritiesUtils;
import ru.dozen.mephi.meta.util.FilterUtils;
import ru.dozen.mephi.meta.web.model.comment.CreateCommentRequestDTO;
import ru.dozen.mephi.meta.web.model.task.ChangeTaskStateRequestDTO;
import ru.dozen.mephi.meta.web.model.task.CreateTaskRequestDTO;
import ru.dozen.mephi.meta.web.model.task.TaskDTO;
import ru.dozen.mephi.meta.web.model.task.TaskFilterDTO;
import ru.dozen.mephi.meta.web.model.task.TaskShortInfoDTO;
import ru.dozen.mephi.meta.web.model.task.UpdateTaskRequestDTO;
import ru.dozen.mephi.meta.web.model.user.UserDTO;

@Service
@RequiredArgsConstructor
public class TasksServiceImpl implements TasksService {

    private final TasksRepository tasksRepository;
    private final TaskMapper taskMapper;
    private final UsersRepository usersRepository;
    private final AutomatedTestManagementSystemClient atmsClient;

    private TaskDTO fillTestStatus(TaskDTO taskDTO) {
        taskDTO.setTestStatus(atmsClient.getTaskTestStatus(taskDTO.getId()));
        return taskDTO;
    }

    private Task getByProjectIdAndKey(Long projectId, String key) {
        return tasksRepository.findByProjectIdAndKey(projectId, key).orElseThrow(
                () -> notFound("No task found with key: " + key + " in project " + projectId));
    }

    private ThrowableProblem notFoundUser(String login) {
        return notFound("No user found with login: " + login);
    }

    private User getUserIfMemberOfProject(long projectId, String login) {
        var user = usersRepository.findByLogin(login)
                .orElseThrow(() -> notFoundUser(login));
        if (!AuthoritiesUtils.isMemberOfProject(user, projectId)) {
            throw badRequest("Specified user " + user.getLogin() + " is not member of project " + projectId);
        }
        return user;
    }

    @Override
    public TaskDTO createTask(long projectId, CreateTaskRequestDTO rq) {
        Task task = taskMapper.fromCreateRequest(rq);
        task.setAuthor(AuthoritiesUtils.getCurrentUser());

        var executorDto = rq.getExecutor();
        if (executorDto != null) {
            task.setExecutor(getUserIfMemberOfProject(projectId, executorDto.getLogin()));
        }

        Optional.ofNullable(rq.getWatchers()).map(it -> it.stream()
                .map(UserDTO::getLogin)
                .map(login -> getUserIfMemberOfProject(projectId, login))
                .toList()).ifPresent(task::setWatchers);

        if (task.getKey() == null) {
            var next = tasksRepository.getMaxKey().orElse(-1) + 1;
            task.setKey("TSK-" + next);
        }

        return taskMapper.toDto(tasksRepository.save(task));
    }

    @Override
    public TaskDTO getTask(long projectId, String key) {
        var task = getByProjectIdAndKey(projectId, key);
        return fillTestStatus(taskMapper.toDto(task));
    }

    @Override
    public TaskDTO updateTask(long projectId, String key, UpdateTaskRequestDTO rq) {
        var task = getByProjectIdAndKey(projectId, key);
        var user = AuthoritiesUtils.getCurrentUser();
        if (!Objects.equals(task.getAuthor().getLogin(), user.getLogin())) {
            throw forbidden("User " + user.getLogin() + " does not have permission to update task");
        }

        taskMapper.updateTask(task, rq);

        var executorDto = rq.getExecutor();
        if (executorDto != null) {
            task.setExecutor(getUserIfMemberOfProject(projectId, executorDto.getLogin()));
        }

        return fillTestStatus(taskMapper.toDto(tasksRepository.save(task)));
    }

    @Override
    public TaskDTO changeTaskState(long projectId, String key, ChangeTaskStateRequestDTO rq) {
        var task = getByProjectIdAndKey(projectId, key);
        var user = AuthoritiesUtils.getCurrentUser();
        if (!(task.getAuthor().getLogin().equals(user.getLogin()) ||
              task.getExecutor().getLogin().equals(user.getLogin()))) {
            throw forbidden("User " + user.getLogin() + " does not have permission to change task's state");
        }
        task.setTaskState(rq.getState());
        return fillTestStatus(taskMapper.toDto(tasksRepository.save(task)));
    }

    private Specification<Task> toSpecification(long projectId, TaskFilterDTO rq) {
        Specification<Task> specification = FilterUtils.eq("project.id", projectId);
        specification = specification.and(FilterUtils.toSpecification(rq));
        return Specification.where(specification);
    }

    @Override
    public List<TaskDTO> searchTasks(long projectId, TaskFilterDTO rq) {
        var specification = toSpecification(projectId, rq);
        return taskMapper.toDto(tasksRepository.findAll(specification)).stream().map(this::fillTestStatus).toList();
    }

    @Override
    public List<TaskShortInfoDTO> searchTasksShortInfo(long projectId, TaskFilterDTO rq) {
        var specification = toSpecification(projectId, rq);
        return taskMapper.toShortInfoDto(tasksRepository.findAll(specification));
    }

    @Override
    public TaskDTO addComment(long projectId, String key, CreateCommentRequestDTO rq) {
        var task = getByProjectIdAndKey(projectId, key);
        var comment = new Comment();
        comment.setText(rq.getText());
        comment.setAuthor(AuthoritiesUtils.getCurrentUser());
        comment.setTask(task);
        task.getComments().add(comment);
        return fillTestStatus(taskMapper.toDto(tasksRepository.save(task)));
    }

    @Override
    public TaskDTO addWatcher(long projectId, String key, UserDTO watcher) {
        var task = getByProjectIdAndKey(projectId, key);
        var user = usersRepository.findByLogin(watcher.getLogin()).orElseThrow(() -> notFoundUser(watcher.getLogin()));

        if (!(task.getAuthor().getLogin().equals(user.getLogin()) ||
              task.getExecutor().getLogin().equals(user.getLogin()) ||
              AuthoritiesUtils.getCurrentUser().getLogin().equals(user.getLogin()))) {
            throw forbidden("Does not have permission to add this watcher to this task");
        }

        var watchers = task.getWatchers();
        if (watchers.stream().noneMatch(u -> u.getLogin().equals(watcher.getLogin()))) {
            watchers.add(user);
            task = tasksRepository.save(task);
        }
        return fillTestStatus(taskMapper.toDto(task));
    }

    @Override
    public TaskDTO removeWatcher(long projectId, String key, UserDTO watcher) {
        var task = getByProjectIdAndKey(projectId, key);
        if (!(task.getAuthor().getLogin().equals(watcher.getLogin()) ||
              task.getExecutor().getLogin().equals(watcher.getLogin()) ||
              AuthoritiesUtils.getCurrentUser().getLogin().equals(watcher.getLogin()))) {
            throw forbidden("Does not have permission to add this watcher to this task");
        }
        var isChanged = task.getWatchers().removeIf(user -> user.getLogin().equals(watcher.getLogin()));
        if (isChanged) {
            task = tasksRepository.save(task);
        }
        return fillTestStatus(taskMapper.toDto(task));
    }
}
