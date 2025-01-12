package ru.dozen.mephi.meta.service;


import java.util.List;
import ru.dozen.mephi.meta.web.model.project.AssignRemoveParticipantRequestDTO;
import ru.dozen.mephi.meta.web.model.project.ChangeProjectStateRequestDTO;
import ru.dozen.mephi.meta.web.model.project.CreateProjectRequestDTO;
import ru.dozen.mephi.meta.web.model.project.ParticipantsDTO;
import ru.dozen.mephi.meta.web.model.project.ProjectDTO;
import ru.dozen.mephi.meta.web.model.project.ProjectFilterDTO;
import ru.dozen.mephi.meta.web.model.project.UpdateRoleRequestDTO;

public interface ProjectService {

    ProjectDTO getProject(long id);
    ProjectDTO createProject(CreateProjectRequestDTO rq);
    ProjectDTO changeProjectState(long projectId, ChangeProjectStateRequestDTO request);
    ProjectDTO updateProject(long projectId, CreateProjectRequestDTO request);
    List<ParticipantsDTO> getParticipants(long projectId);
    ProjectDTO assignParticipant(long projectId, AssignRemoveParticipantRequestDTO request);
    List<ParticipantsDTO> removeParticipant(long projectId, AssignRemoveParticipantRequestDTO request);

    ParticipantsDTO updateParticipantRole(long projectId, UpdateRoleRequestDTO request);
    List<ProjectDTO> searchProjects(ProjectFilterDTO filter);
}