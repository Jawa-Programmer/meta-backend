package ru.dozen.mephi.meta.service.mapper;

import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.dozen.mephi.meta.domain.Project;
import ru.dozen.mephi.meta.domain.Task;
import ru.dozen.mephi.meta.web.model.task.CreateTaskRequestDTO;
import ru.dozen.mephi.meta.web.model.task.ProjectShortInfoDTO;
import ru.dozen.mephi.meta.web.model.task.TaskDTO;
import ru.dozen.mephi.meta.web.model.task.TaskShortInfoDTO;
import ru.dozen.mephi.meta.web.model.task.UpdateTaskRequestDTO;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {UserMapper.class}, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface TaskMapper {

    TaskDTO toDto(Task task);

    List<TaskDTO> toDto(List<Task> tasks);

    List<TaskShortInfoDTO> toShortInfoDto(List<Task> tasks);

    @Mapping(target = "watchers", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "executor", ignore = true)
    @Mapping(target = "taskState", constant = "NEW")
    Task fromCreateRequest(CreateTaskRequestDTO rq);

    @Mapping(target = "executor", ignore = true)
    void updateTask(@MappingTarget Task target, UpdateTaskRequestDTO source);

    ProjectShortInfoDTO toProjectShortInfoDto(Project project);
}
