package ru.dozen.mephi.meta.service.mapper;

import ru.dozen.mephi.meta.domain.Project;
import ru.dozen.mephi.meta.web.model.project.CreateProjectRequestDTO;
import ru.dozen.mephi.meta.web.model.project.ProjectDTO;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, uses = {UserMapper.class}, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ProjectMapper {
    @Mapping(source = "projectState", target = "state")
    ProjectDTO toDto(Project project);

    List<ProjectDTO> toDto(List<Project> projects);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roleRecords", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "projectState", constant = "ACTIVE")
    Project fromCreateRequest(CreateProjectRequestDTO rq);

    @Mapping(target = "director", ignore = true)
    void updateProject(@MappingTarget Project project, CreateProjectRequestDTO source);
}
