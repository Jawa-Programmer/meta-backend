package ru.dozen.mephi.meta.service.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.dozen.mephi.meta.domain.Project;
import ru.dozen.mephi.meta.web.model.project.ProjectDTO;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProjectMapper {

    ProjectDTO toDto(Project task);

    List<ProjectDTO> toDto(List<Project> tasks);

}
