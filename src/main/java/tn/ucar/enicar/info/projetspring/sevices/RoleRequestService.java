package tn.ucar.enicar.info.projetspring.sevices;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import tn.ucar.enicar.info.projetspring.auth.RoleRequestDTO;
import tn.ucar.enicar.info.projetspring.auth.RoleRequestInputDTO;
import tn.ucar.enicar.info.projetspring.entities.*;
import tn.ucar.enicar.info.projetspring.repositories.*;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleRequestService {
    private final RoleRequestRepository roleRequestRepository;
    private final userRepo userRepository;
    private final TaskRepository taskRepository;
    private final TeamRepository teamRepository;
    private final EventRepository eventRepository;

    // Étape 2 : Créer une demande pour devenir RESPONSIBLE
    public RoleRequestDTO createRoleRequest(RoleRequestInputDTO input, User user) {
        Role requestedRole = Role.valueOf(input.getRequestedRole());

        // Valider l'événement
        event event = input.getEventId() != null
                ? eventRepository.findById(input.getEventId())
                .orElseThrow(() -> new IllegalArgumentException("Event not found"))
                : null;

        // Valider l'équipe pour RESPONSIBLE
        Team team = null;
        if (requestedRole == Role.RESPONSIBLE) {
            if (input.getTeamId() == null) {
                throw new IllegalArgumentException("Team ID is required for RESPONSIBLE role");
            }
            team = teamRepository.findById(input.getTeamId())
                    .orElseThrow(() -> new IllegalArgumentException("Team not found"));

        }

        RoleRequest roleRequest = RoleRequest.builder()
                .requestedRole(requestedRole)
                .status(RequestStatus.PENDING)
                .user(user)
                .event(event)
                .team(team)
                .task(input.getTaskId() != null ? taskRepository.findById(input.getTaskId())
                        .orElseThrow(() -> new IllegalArgumentException("Task not found")) : null)
                .build();

        RoleRequest savedRequest = roleRequestRepository.save(roleRequest);
        return mapToDTO(savedRequest);
    }

    // Étape 3 : Admin approuve/rejette une demande de RESPONSIBLE
    @PreAuthorize("hasRole('ADMIN')")
    public RoleRequestDTO reviewRoleRequest(Long requestId, String status, String comments) {
        RoleRequest roleRequest = roleRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Role request not found"));

        RequestStatus requestStatus = RequestStatus.valueOf(status.toUpperCase());
        roleRequest.setStatus(requestStatus);
        roleRequest.setComments(comments);

        if (requestStatus == RequestStatus.APPROVED && roleRequest.getRequestedRole() == Role.RESPONSIBLE) {
            User user = roleRequest.getUser();
            user.setRole(Role.RESPONSIBLE);
            if (roleRequest.getEvent() != null) {
                roleRequest.getEvent().getResponsibles().add(user);
                eventRepository.save(roleRequest.getEvent());
            }
            if (roleRequest.getTeam() != null) {
                roleRequest.getTeam().setResponsible(user);
                teamRepository.save(roleRequest.getTeam());
            }
            userRepository.save(user);
        }

        RoleRequest updatedRequest = roleRequestRepository.save(roleRequest);
        return mapToDTO(updatedRequest);
    }

    // Étape 5 : Créer une demande pour devenir VOLUNTARY
    public RoleRequestDTO createVolunteerRequest(RoleRequestInputDTO input, User user) {
        if (input.getTaskId() == null) {
            throw new IllegalArgumentException("Task ID is required for volunteer request");
        }

        task task = taskRepository.findById(input.getTaskId())
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
// Récupérer l'équipe associée à la tâche
        Team team = task.getTeam();
        if (team == null) {
            throw new IllegalArgumentException("Task is not associated with a team");
        }
        RoleRequest roleRequest = RoleRequest.builder()
                .requestedRole(Role.VOLUNTARY)
                .status(RequestStatus.PENDING)
                .user(user)
                .task(task)
                .event(task.getEvent())
                .team(team)
                .build();

        RoleRequest savedRequest = roleRequestRepository.save(roleRequest);
        return mapToDTO(savedRequest);
    }

    // Étape 6 : Responsible approuve/rejette une demande de VOLUNTARY
    public RoleRequestDTO reviewVolunteerRequest(Long requestId, String status, String comments, User responsible) {
        RoleRequest roleRequest = roleRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Role request not found"));

        task task = roleRequest.getTask();
        if (task == null || !task.getResponsible().getId().equals(responsible.getId())) {
            throw new IllegalStateException("Only the task responsible can review this request");
        }

        RequestStatus requestStatus = RequestStatus.valueOf(status.toUpperCase());
        roleRequest.setStatus(requestStatus);
        roleRequest.setComments(comments);

        if (requestStatus == RequestStatus.APPROVED) {
            User user = roleRequest.getUser();
            user.setRole(Role.VOLUNTARY);
            task.getVolunteers().add(user);
            user.getVolunteerTasks().add(task);
            taskRepository.save(task);
            Team team = roleRequest.getTeam();
            if (team != null) {
                team.getVolunteers().add(user);
                teamRepository.save(team);
            }
            userRepository.save(user);
        }


        RoleRequest updatedRequest = roleRequestRepository.save(roleRequest);
        return mapToDTO(updatedRequest);
    }

    // Liste des demandes en attente
    public List<RoleRequestDTO> getPendingRequests(User user) {
        if (user.getRole() == Role.ADMIN) {
            return roleRequestRepository.findByStatus(RequestStatus.PENDING)
                    .stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());
        } else if (user.getRole() == Role.RESPONSIBLE) {
            List<Long> taskIds = taskRepository.findByResponsible(user)
                    .stream()
                    .map(task::getId)
                    .collect(Collectors.toList());
            return roleRequestRepository.findByStatusAndTaskIdIn(RequestStatus.PENDING, taskIds)
                    .stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    private RoleRequestDTO mapToDTO(RoleRequest roleRequest) {
        return RoleRequestDTO.builder()
                .id(roleRequest.getId())
                .requestedRole(roleRequest.getRequestedRole().name())
                .status(roleRequest.getStatus().name())
                .comments(roleRequest.getComments())
                .userEmail(roleRequest.getUser().getEmail())
                .taskId(roleRequest.getTask() != null ? roleRequest.getTask().getId() : null)
                .teamId(roleRequest.getTeam() != null ? roleRequest.getTeam().getId() : null)
                .eventId(roleRequest.getEvent() != null ? roleRequest.getEvent().getId() : null)
                .build();
    }
}