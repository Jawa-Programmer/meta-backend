package ru.dozen.mephi.meta.service;

import java.util.List;
import ru.dozen.mephi.meta.web.model.comment.CreateCommentRequestDTO;
import ru.dozen.mephi.meta.web.model.task.ChangeTaskStateRequestDTO;
import ru.dozen.mephi.meta.web.model.task.CreateTaskRequestDTO;
import ru.dozen.mephi.meta.web.model.task.TaskDTO;
import ru.dozen.mephi.meta.web.model.task.TaskFilterDTO;
import ru.dozen.mephi.meta.web.model.task.TaskShortInfoDTO;
import ru.dozen.mephi.meta.web.model.task.UpdateTaskRequestDTO;
import ru.dozen.mephi.meta.web.model.user.UserDTO;

public interface TasksService {

    TaskDTO createTask(long projectId, CreateTaskRequestDTO rq);

    TaskDTO getTask(long projectId, String key);

    TaskDTO updateTask(long projectId, String key, UpdateTaskRequestDTO rq);

    TaskDTO changeTaskState(long projectId, String key, ChangeTaskStateRequestDTO rq);

    List<TaskDTO> searchTasks(long projectId, TaskFilterDTO rq);

    List<TaskShortInfoDTO> searchTasksShortInfo(long projectId, TaskFilterDTO rq);

    TaskDTO addComment(long projectId, String key, CreateCommentRequestDTO rq);

    TaskDTO addWatcher(long projectId, String key, UserDTO watcher);

    TaskDTO removeWatcher(long projectId, String key, UserDTO watcher);
}
