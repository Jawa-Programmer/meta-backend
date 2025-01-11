package ru.dozen.mephi.meta.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.dozen.mephi.meta.domain.Project;
import ru.dozen.mephi.meta.domain.RoleRecord;
import ru.dozen.mephi.meta.domain.User;
import ru.dozen.mephi.meta.repository.ProjectsRepository;
import ru.dozen.mephi.meta.repository.RoleRecordsRepository;
import ru.dozen.mephi.meta.repository.UserRolesRepository;
import ru.dozen.mephi.meta.repository.UsersRepository;
import ru.dozen.mephi.meta.service.ProjectService;
import ru.dozen.mephi.meta.service.mapper.ProjectMapper;
import ru.dozen.mephi.meta.service.mapper.RoleRecordMapper;
import ru.dozen.mephi.meta.util.AuthoritiesUtils;
import ru.dozen.mephi.meta.util.FilterUtils;
import ru.dozen.mephi.meta.util.ProblemUtils;
import ru.dozen.mephi.meta.web.model.project.*;

import java.util.List;

import static ru.dozen.mephi.meta.util.ProblemUtils.notFound;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectsRepository projectsRepository;
    private final ProjectMapper projectMapper;
    private final UsersRepository usersRepository;
    private final UserRolesRepository userRolesRepository;
    private final RoleRecordsRepository roleRecordsRepository;
    private final RoleRecordMapper roleRecordMapper;

    @Override
    public ProjectDTO getProject(long id) {
        var project = getProjectById(id);
        return projectMapper.toDto(project);
    }

    @Override
    public ProjectDTO createProject(CreateProjectRequestDTO rq) {

        var project = projectMapper.fromCreateRequest(rq);

        var directorDto = rq.getDirector();
        if (directorDto != null) {
            project.setDirector(getUser(directorDto.getLogin()));
        }

        try {
            return projectMapper.toDto(projectsRepository.save(project));
        } catch (DataIntegrityViolationException e) {
            throw ProblemUtils.badRequest("Project with title: " + project.getTitle() + " already exists");
        }
    }

    @Override
    public ProjectDTO changeProjectState(long projectId, ChangeProjectStateRequestDTO request) {
        var user = AuthoritiesUtils.getCurrentUser();

        var project = projectsRepository.findById(projectId).orElseThrow(
                () -> new EntityNotFoundException("Project with ID " + projectId + " not found")
        );

        if (!user.getId().equals(project.getDirector().getId())) {
            throw new SecurityException("You do not have permission to change the state of this project");
        }

        project.setProjectState(request.getState());
        projectsRepository.save(project);

        return projectMapper.toDto(project);
    }

    @Override
    public ProjectDTO updateProject(long projectId, CreateProjectRequestDTO request) {
        var project = projectsRepository.findById(projectId).orElseThrow(
                () -> new EntityNotFoundException("Project with ID " + projectId + " not found"));

        projectMapper.updateProject(project, request);

        var directorDto = request.getDirector();
        if (directorDto != null) {
            project.setDirector(getUser(directorDto.getLogin()));
        }

        return projectMapper.toDto(projectsRepository.save(project));
    }

    @Override
    public List<ParticipantsDTO> getParticipants(long projectId) {
        var project = projectsRepository.findById(projectId).orElseThrow(
                () -> new EntityNotFoundException("Project with ID " + projectId + " not found")
        );

        return project.getRoleRecords().stream()
                .map(roleRecord -> {
                    var user = roleRecord.getUser();
                    var role = roleRecord.getRole();
                    return new ParticipantsDTO(
                            user.getId(),
                            user.getFio(),
                            user.getLogin(),
                            role.getRoleName()
                    );
                })
                .toList();
    }

    @Override
    public ProjectDTO assignParticipant(long projectId, AssignRemoveParticipantRequestDTO request) {
        var project = projectsRepository.findById(projectId).orElseThrow(
                () -> new EntityNotFoundException("Проект с ID " + projectId + " не найден")
        );

        var user = usersRepository.findById(request.getUserId()).orElseThrow(
                () -> new EntityNotFoundException("Пользователь с ID " + request.getUserId() + " не найден")
        );

        var userRole = userRolesRepository.findById(request.getRoleId()).orElseThrow(
                () -> new EntityNotFoundException("Роль с ID " + request.getRoleId() + " не найдена")
        );

        boolean alreadyExists = project.getRoleRecords().stream()
                .anyMatch(roleRecord -> roleRecord.getUser().equals(user) && roleRecord.getRole().equals(userRole));
        if (alreadyExists) {
            throw new IllegalArgumentException("Пользователь уже назначен с данной ролью в проекте");
        }

        RoleRecord roleRecord = new RoleRecord();
        roleRecord.setProject(project);
        roleRecord.setUser(user);
        roleRecord.setRole(userRole);

        project.getRoleRecords().add(roleRecord);

        return projectMapper.toDto(projectsRepository.save(project));
    }

    @Override
    public List<ParticipantsDTO> removeParticipant(long projectId, AssignRemoveParticipantRequestDTO request) {
        var project = projectsRepository.findById(projectId).orElseThrow(
                () -> new EntityNotFoundException("Проект с ID " + projectId + " не найден")
        );

        var existingRoleRecord = project.getRoleRecords().stream()
                .filter(roleRecord ->
                        roleRecord.getUser().getId().equals(request.getUserId()) &&
                                roleRecord.getRole().getId().equals(request.getRoleId()))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Участник с указанным ролью и ID в проекте не найден"));

        project.getRoleRecords().remove(existingRoleRecord);

        roleRecordsRepository.delete(existingRoleRecord);

        projectsRepository.save(project);

        return project.getRoleRecords().stream()
                .map(roleRecord -> {
                    var user = roleRecord.getUser();
                    var role = roleRecord.getRole();
                    return new ParticipantsDTO(
                            user.getId(),
                            user.getFio(),
                            user.getLogin(),
                            role.getRoleName()
                    );
                })
                .toList();
    }

    @Override
    public RoleRecordDTO updateParticipantRole(long projectId, UpdateRoleRequestDTO request) {
        var project = projectsRepository.findById(projectId).orElseThrow(
                () -> new EntityNotFoundException("Проект с ID " + projectId + " не найден")
        );

        var existingRoleRecord = project.getRoleRecords().stream()
                .filter(roleRecord ->
                        roleRecord.getUser().getId().equals(request.getUserId()) &&
                                roleRecord.getRole().getId().equals(request.getOldRoleId()))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Участник с указанной ролью в проекте не найден"));

        var newRole = userRolesRepository.findById(request.getNewRoleId()).orElseThrow(
                () -> new EntityNotFoundException("Роль с ID " + request.getNewRoleId() + " не найдена")
        );

        existingRoleRecord.setRole(newRole);

        var updatedRoleRecord = roleRecordsRepository.save(existingRoleRecord);

        return roleRecordMapper.toDto(updatedRoleRecord);
    }

    @Override
    public List<ProjectDTO> searchProjects(ProjectFilterDTO filter) {
        Specification<Project> specification = FilterUtils.toSpecification(filter);
        List<Project> projects = projectsRepository.findAll(specification);
        return projects.stream().map(projectMapper::toDto).toList();
    }

    private Project getProjectById(Long id) {
        return projectsRepository.findById(id).orElseThrow(
                () -> notFound("No project found with id: " + id));
    }

    private User getUser(String login) {
        return usersRepository.findByLogin(login).orElseThrow(() -> notFound("No user found with login: " + login));
    }
}
