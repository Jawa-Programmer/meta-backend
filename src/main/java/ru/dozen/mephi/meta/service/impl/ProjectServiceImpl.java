package ru.dozen.mephi.meta.service.impl;

import static ru.dozen.mephi.meta.util.ProblemUtils.badRequest;
import static ru.dozen.mephi.meta.util.ProblemUtils.forbidden;
import static ru.dozen.mephi.meta.util.ProblemUtils.notFound;

import java.util.Comparator;
import java.util.List;
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
import ru.dozen.mephi.meta.service.mapper.UserMapper;
import ru.dozen.mephi.meta.util.AuthoritiesUtils;
import ru.dozen.mephi.meta.util.FilterUtils;
import ru.dozen.mephi.meta.web.model.project.AssignRemoveParticipantRequestDTO;
import ru.dozen.mephi.meta.web.model.project.ChangeProjectStateRequestDTO;
import ru.dozen.mephi.meta.web.model.project.CreateProjectRequestDTO;
import ru.dozen.mephi.meta.web.model.project.ParticipantsDTO;
import ru.dozen.mephi.meta.web.model.project.ProjectDTO;
import ru.dozen.mephi.meta.web.model.project.ProjectFilterDTO;
import ru.dozen.mephi.meta.web.model.project.UpdateRoleRequestDTO;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectsRepository projectsRepository;
    private final ProjectMapper projectMapper;
    private final UsersRepository usersRepository;
    private final UserMapper userMapper;
    private final UserRolesRepository userRolesRepository;
    private final RoleRecordsRepository roleRecordsRepository;

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
            throw badRequest("Проект с названием \"" + project.getTitle() + "\" уже существует");
        }
    }

    @Override
    public ProjectDTO changeProjectState(long projectId, ChangeProjectStateRequestDTO request) {
        var user = AuthoritiesUtils.getCurrentUser();

        var project = projectsRepository.findById(projectId).orElseThrow(
                () -> notFound("Не найден проект " + projectId)
        );

        if (!user.getId().equals(project.getDirector().getId())) {
            throw forbidden("Текущий пользователь не имеет права менять данный проект");
        }

        project.setProjectState(request.getState());
        projectsRepository.save(project);

        return projectMapper.toDto(project);
    }

    @Override
    public ProjectDTO updateProject(long projectId, CreateProjectRequestDTO request) {
        var project = projectsRepository.findById(projectId).orElseThrow(
                () -> notFound("Не найден проект " + projectId)
        );

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
                () -> notFound("Не найден проект " + projectId)
        );

        return project.getRoleRecords().stream()
                .map(roleRecord -> {
                    var user = roleRecord.getUser();
                    var role = roleRecord.getRole();
                    return new ParticipantsDTO(
                            user.getId(),
                            user.getFio(),
                            user.getLogin(),
                            userMapper.toDto(user),
                            role.getRoleName()
                    );
                })
                .toList();
    }

    @Override
    public ProjectDTO assignParticipant(long projectId, AssignRemoveParticipantRequestDTO request) {
        var project = projectsRepository.findById(projectId).orElseThrow(
                () -> notFound("Не найден проект " + projectId)
        );

        var user = usersRepository.findById(request.getUserId()).orElseThrow(
                () -> notFound("Пользователь с ID " + request.getUserId() + " не найден")
        );

        var userRole = userRolesRepository.findById(request.getRoleId()).orElseThrow(
                () -> notFound("Роль с ID " + request.getRoleId() + " не найдена")
        );

        boolean alreadyExists = project.getRoleRecords().stream()
                .anyMatch(roleRecord -> roleRecord.getUser().equals(user) && roleRecord.getRole().equals(userRole));
        if (alreadyExists) {
            throw badRequest("Пользователь уже назначен с данной ролью в проекте");
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
                () -> notFound("Проект с ID " + projectId + " не найден")
        );

        var existingRoleRecord = project.getRoleRecords().stream()
                .filter(roleRecord ->
                        roleRecord.getUser().getId().equals(request.getUserId()) &&
                                roleRecord.getRole().getId().equals(request.getRoleId()))
                .findFirst()
                .orElseThrow(() -> notFound("Участник с указанным ролью и ID в проекте не найден"));

        project.getRoleRecords().remove(existingRoleRecord);

        project = projectsRepository.save(project);

        roleRecordsRepository.deleteById(existingRoleRecord.getId());

        return project.getRoleRecords().stream()
                .map(roleRecord -> {
                    var user = roleRecord.getUser();
                    var role = roleRecord.getRole();
                    return new ParticipantsDTO(
                            user.getId(),
                            user.getFio(),
                            user.getLogin(),
                            userMapper.toDto(user),
                            role.getRoleName()
                    );
                })
                .toList();
    }

    @Override
    public ParticipantsDTO updateParticipantRole(long projectId, UpdateRoleRequestDTO request) {
        var project = projectsRepository.findById(projectId).orElseThrow(
                () -> notFound("Проект с ID " + projectId + " не найден")
        );

        var existingRoleRecord = project.getRoleRecords().stream()
                .filter(roleRecord ->
                        roleRecord.getUser().getId().equals(request.getUserId()) &&
                                roleRecord.getRole().getId().equals(request.getOldRoleId()))
                .findFirst()
                .orElseThrow(() -> notFound("Участник с указанной ролью в проекте не найден"));

        var newRole = userRolesRepository.findById(request.getNewRoleId()).orElseThrow(
                () -> notFound("Роль с ID " + request.getNewRoleId() + " не найдена")
        );

        existingRoleRecord.setRole(newRole);

        projectsRepository.save(project);

        return new ParticipantsDTO(
                existingRoleRecord.getUser().getId(),
                existingRoleRecord.getUser().getFio(),
                existingRoleRecord.getUser().getLogin(),
                userMapper.toDto(existingRoleRecord.getUser()),
                existingRoleRecord.getRole().getRoleName());
    }

    @Override
    public List<ProjectDTO> searchProjects(ProjectFilterDTO filter) {
        Specification<Project> specification = FilterUtils.toSpecification(filter);
        List<Project> projects = projectsRepository.findAll(specification);
        var user = AuthoritiesUtils.getCurrentUser();
        if (!AuthoritiesUtils.hasAnyRole(user, "ROLE_ADMIN", "ROLE_SUPERUSER")) {
            projects = projects.stream()
                    .filter(it -> AuthoritiesUtils.isMemberOfProject(user, it.getId()))
                    .toList();
        }
        return projects.stream().map(projectMapper::toDto).sorted(Comparator.comparingLong(ProjectDTO::getId)).toList().reversed();
    }

    private Project getProjectById(Long id) {
        return projectsRepository.findById(id).orElseThrow(
                () -> notFound("Не найден проект " + id));
    }

    private User getUser(String login) {
        return usersRepository.findByLogin(login)
                .orElseThrow(() -> notFound("Не найден пользователь с логином " + login));
    }
}
