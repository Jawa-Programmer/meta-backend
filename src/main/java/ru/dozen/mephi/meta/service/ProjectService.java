package ru.dozen.mephi.meta.service;


import ru.dozen.mephi.meta.web.model.project.*;

import java.util.List;

public interface ProjectService {

    ProjectDTO getProject(long id);
    ProjectDTO createProject(CreateProjectRequestDTO rq);
    ProjectDTO changeProjectState(long projectId, ChangeProjectStateRequestDTO request);
    ProjectDTO updateProject(long projectId, CreateProjectRequestDTO request);
    List<ParticipantsDTO> getParticipants(long projectId);
    ProjectDTO assignParticipant(long projectId, AssignRemoveParticipantRequestDTO request);
    List<ParticipantsDTO> removeParticipant(long projectId, AssignRemoveParticipantRequestDTO request);
    RoleRecordDTO updateParticipantRole(long projectId, UpdateRoleRequestDTO request);
    List<ProjectDTO> searchProjects(ProjectFilterDTO filter);
}