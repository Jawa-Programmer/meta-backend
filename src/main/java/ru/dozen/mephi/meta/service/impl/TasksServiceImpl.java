package ru.dozen.mephi.meta.service.impl;

import static ru.dozen.mephi.meta.util.ProblemUtils.badRequest;
import static ru.dozen.mephi.meta.util.ProblemUtils.forbidden;
import static ru.dozen.mephi.meta.util.ProblemUtils.notFound;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.zalando.problem.ThrowableProblem;
import ru.dozen.mephi.meta.client.AutomatedTestManagementSystemClient;
import ru.dozen.mephi.meta.domain.Comment;
import ru.dozen.mephi.meta.domain.Task;
import ru.dozen.mephi.meta.domain.User;
import ru.dozen.mephi.meta.repository.ProjectsRepository;
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
    private final ProjectsRepository projectsRepository;
    private final AutomatedTestManagementSystemClient atmsClient;

    private TaskDTO fillTestStatus(TaskDTO taskDTO) {
        taskDTO.setTestStatus(atmsClient.getTaskTestStatus(taskDTO.getId()));
        return taskDTO;
    }

    private Task getByProjectIdAndKey(Long projectId, String key) {
        return tasksRepository.findByProjectIdAndKey(projectId, key).orElseThrow(
                () -> notFound("Не найдена задача с ключом " + key + " в проекте " + projectId));
    }

    private ThrowableProblem notFoundUser(String login) {
        return notFound("Не найден пользователь с логином " + login);
    }

    private User getUserIfMemberOfProject(long projectId, String login) {
        var user = usersRepository.findByLogin(login)
                .orElseThrow(() -> notFoundUser(login));
        if (!AuthoritiesUtils.isMemberOfProject(user, projectId)) {
            throw badRequest("Пользователь " + user.getLogin() + " не является участником проекта " + projectId);
        }
        return user;
    }

    @Override
    public TaskDTO createTask(long projectId, CreateTaskRequestDTO rq) {
        var key = rq.getKey();
        if (StringUtils.isBlank(key)) {
            var next = tasksRepository.getMaxKey().orElse(-1) + 1;
            key = "TSK-" + next;
        } else {
            key = key.toUpperCase();
            if (tasksRepository.existsByKey(key)) {
                throw badRequest("Ключ " + key + " уже занят");
            }
        }
        Task task = taskMapper.fromCreateRequest(rq);
        task.setKey(key);
        task.setAuthor(AuthoritiesUtils.getCurrentUser());

        var executorDto = rq.getExecutor();
        if (executorDto != null) {
            task.setExecutor(getUserIfMemberOfProject(projectId, executorDto.getLogin()));
        }

        Optional.ofNullable(rq.getWatchers()).map(it -> it.stream()
                .map(UserDTO::getLogin)
                .map(login -> getUserIfMemberOfProject(projectId, login))
                .toList()).ifPresent(task::setWatchers);

        task.setProject(projectsRepository.findById(projectId).orElseThrow());

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
            throw forbidden("Текущий пользователь не имеет прав менять данную задачу");
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
            throw forbidden("Текущий пользователь не имеет прав менять данную задачу");
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
        var current = AuthoritiesUtils.getCurrentUser();

        if (!(task.getAuthor().getLogin().equals(current.getLogin()) ||
              task.getExecutor().getLogin().equals(current.getLogin()) ||
              current.getLogin().equals(user.getLogin()))) {
            throw forbidden("Текущий пользователь не имеет права добавить данного наблюдателя в данную задачу");
        }
        if (!AuthoritiesUtils.isMemberOfProject(user, projectId)) {
            throw badRequest( "Данный пользователь не является участником проекта и не может быть назначен наблюдателем");
        }

        var watchers = task.getWatchers();
        if (watchers.stream().noneMatch(u -> u.getId().equals(user.getId()))) {
            watchers.add(user);
            task = tasksRepository.save(task);
        } else {
            throw badRequest("Данный пользователь уже является наблюдателем в данной задаче");
        }
        return fillTestStatus(taskMapper.toDto(task));
    }

    @Override
    public TaskDTO removeWatcher(long projectId, String key, UserDTO watcher) {
        var task = getByProjectIdAndKey(projectId, key);
        var current = AuthoritiesUtils.getCurrentUser();
        if (!(task.getAuthor().getLogin().equals(current.getLogin()) ||
              task.getExecutor().getLogin().equals(current.getLogin()) ||
              current.getLogin().equals(watcher.getLogin()))) {
            throw forbidden("Текущий пользователь не имеет права удалить данного наблюдателя из данной задачи");
        }
        var isChanged = task.getWatchers().removeIf(user -> user.getLogin().equals(watcher.getLogin()));
        if (isChanged) {
            task = tasksRepository.save(task);
        }
        return fillTestStatus(taskMapper.toDto(task));
    }
}
