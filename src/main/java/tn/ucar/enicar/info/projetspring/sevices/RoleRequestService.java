package tn.ucar.enicar.info.projetspring.sevices;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tn.ucar.enicar.info.projetspring.auth.RoleRequestDTO;
import tn.ucar.enicar.info.projetspring.auth.RoleRequestInputDTO;
import tn.ucar.enicar.info.projetspring.entities.*;
import tn.ucar.enicar.info.projetspring.repositories.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleRequestService {
    private final RoleRequestRepository roleRequestRepository;
    private final userRepo userRepository;
    private final TaskRepository taskRepository;
    private final TeamRepository teamRepository;
    private final EventRepository eventRepository;
    private final CandidatureRepository candidatureRepository;
    private final String uploadDir = "Uploads/cvs/";

    public RoleRequestDTO createRoleRequest(RoleRequestInputDTO input, String experience, String description, User user) {
        Role requestedRole = Role.valueOf(input.getRequestedRole());

        if (requestedRole == Role.RESPONSIBLE && input.getEventId() == null) {
            throw new IllegalArgumentException("Event ID is required for RESPONSIBLE role");
        }
        event event = eventRepository.findById(input.getEventId())
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        Team team = null;
        if (requestedRole == Role.RESPONSIBLE) {
            if (input.getTeamId() == null) {
                throw new IllegalArgumentException("Team ID is required for RESPONSIBLE role");
            }
            team = teamRepository.findById(input.getTeamId())
                    .orElseThrow(() -> new IllegalArgumentException("Team not found"));

            if (team.getEvent() == null || team.getEvent().getId() != input.getEventId()) {
                throw new IllegalArgumentException("Team is not associated with the specified event");
            }
        }

        Candidature candidature = null;
        if (requestedRole == Role.RESPONSIBLE) {
            if (description == null || description.isBlank()) {
                throw new IllegalArgumentException("Description is required for RESPONSIBLE role application");
            }
            if (experience == null || experience.isBlank()) {
                throw new IllegalArgumentException("Experience is required for RESPONSIBLE role application");
            }

            candidature = Candidature.builder()
                    .description(description)
                    .experience(experience)
                    .submittedAt(LocalDateTime.now())
                    .build();
            candidature = candidatureRepository.save(candidature);
        }

        RoleRequest roleRequest = RoleRequest.builder()
                .requestedRole(requestedRole)
                .status(RequestStatus.PENDING)
                .user(user)
                .event(event)
                .team(team)
                .task(input.getTaskId() != null ? taskRepository.findById(input.getTaskId())
                        .orElseThrow(() -> new IllegalArgumentException("Task not found")) : null)
                .candidature(candidature)
                .build();

        RoleRequest savedRequest = roleRequestRepository.save(roleRequest);
        return mapToDTO(savedRequest);
    }

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

    public RoleRequestDTO createVolunteerRequest(RoleRequestInputDTO input, MultipartFile cv, User user) {
        if (!input.getRequestedRole().equals(Role.VOLUNTARY.name())) {
            throw new IllegalArgumentException("Invalid role requested for volunteer application");
        }

        task task = taskRepository.findById(input.getTaskId())
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        event event = task.getEvent();
        if (event == null) {
            throw new IllegalArgumentException("Task is not associated with an event");
        }

        String cvPath = saveCvFile(cv);

        Candidature candidature = Candidature.builder()
                .description(input.getDescription())
                .cvPath(cvPath)
                .submittedAt(LocalDateTime.now())
                .build();
        Candidature savedCandidature = candidatureRepository.save(candidature);

        RoleRequest roleRequest = RoleRequest.builder()
                .requestedRole(Role.VOLUNTARY)
                .status(RequestStatus.PENDING)
                .user(user)
                .event(event)
                .task(task)
                .candidature(savedCandidature)
                .build();

        RoleRequest savedRequest = roleRequestRepository.save(roleRequest);
        return mapToDTO(savedRequest);
    }

    @PreAuthorize("hasRole('RESPONSIBLE')")
    public RoleRequestDTO reviewVolunteerRequest(Long requestId, String status, String comments) {
        RoleRequest roleRequest = roleRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Role request not found"));

        RequestStatus requestStatus = RequestStatus.valueOf(status.toUpperCase());
        roleRequest.setStatus(requestStatus);
        roleRequest.setComments(comments);

        if (requestStatus == RequestStatus.APPROVED && roleRequest.getRequestedRole() == Role.VOLUNTARY) {
            User user = roleRequest.getUser();
            user.setRole(Role.VOLUNTARY);

            if (roleRequest.getTeam() != null) {
                roleRequest.getTeam().setResponsible(user);
                teamRepository.save(roleRequest.getTeam());
            }
            if (roleRequest.getTask() != null) {
                roleRequest.getTask().getVolunteers().add(user);
                taskRepository.save(roleRequest.getTask());
            }

            userRepository.save(user);
        }
        RoleRequest updatedRequest = roleRequestRepository.save(roleRequest);
        return mapToDTO(updatedRequest);
    }

    public List<RoleRequestDTO> getPendingVolunteerCandidatures(User responsible) {
        List<Long> taskIds = taskRepository.findByResponsible(responsible)
                .stream()
                .map(task::getId)
                .collect(Collectors.toList());
        return roleRequestRepository.findByStatusAndTaskIdIn(RequestStatus.PENDING, taskIds)
                .stream()
                .filter(r -> r.getRequestedRole() == Role.VOLUNTARY)
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

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

    private String saveCvFile(MultipartFile cv) {
        try {
            File uploadDirFile = new File(uploadDir);
            if (!uploadDirFile.exists()) {
                uploadDirFile.mkdirs();
            }

            String originalFileName = cv.getOriginalFilename();
            String fileExtension = originalFileName != null ? originalFileName.substring(originalFileName.lastIndexOf(".")) : ".pdf";
            String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
            Path filePath = Paths.get(uploadDir, uniqueFileName);

            Files.write(filePath, cv.getBytes());
            return filePath.toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to save CV file", e);
        }
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
                .description(roleRequest.getCandidature() != null ? roleRequest.getCandidature().getDescription() : null)
                .cvPath(roleRequest.getCandidature() != null ? roleRequest.getCandidature().getCvPath() : null)
                .submittedAt(roleRequest.getCandidature() != null ? roleRequest.getCandidature().getSubmittedAt() : null)
                .build();
    }
}