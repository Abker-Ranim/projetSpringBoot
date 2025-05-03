package tn.ucar.enicar.info.projetspring.sevices;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.ucar.enicar.info.projetspring.entities.*;
import tn.ucar.enicar.info.projetspring.repositories.TaskRepository;
import tn.ucar.enicar.info.projetspring.repositories.TeamRepository;
import tn.ucar.enicar.info.projetspring.repositories.userRepo;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamService {
    private final TeamRepository teamRepository;
    private final EventService eventService;
    private final userRepo userRepository;
    private final TaskRepository taskRepository;

    public TeamDTO createTeam(Team team, Long eventId) {
        event event = eventService.getEventById(eventId);
        team.setEvent(event);
        team.setVolunteers(new HashSet<>());
        team.setTasks(new HashSet<>());
        Team savedTeam = teamRepository.save(team);
        return mapToDTO(savedTeam);
    }

    public TeamDTO updateTeam(Long teamId, Team updatedTeam, Long eventId) {
        Team existingTeam = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Team not found"));

        existingTeam.setName(updatedTeam.getName());
        existingTeam.setDescription(updatedTeam.getDescription());

        if (eventId != null) {
            event event = eventService.getEventById(eventId);
            existingTeam.setEvent(event);
        }

        Team savedTeam = teamRepository.save(existingTeam);
        return mapToDTO(savedTeam);
    }

    public void deleteTeam(Long teamId) {
        if (!teamRepository.existsById(teamId)) {
            throw new IllegalArgumentException("Team not found");
        }
        teamRepository.deleteById(teamId);
    }
    public List<TeamDTO> getAllTeams() {
        return teamRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    public List<TeamDTO> getTeamsByEventId(Long eventId) {
        event event = eventService.getEventById(eventId);
        List<Team> teams = teamRepository.findByEvent(event);
        return teams.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    private TeamDTO mapToDTO(Team team) {
        Set<Long> taskIds = team.getTasks() != null
                ? team.getTasks().stream().map(task::getId).collect(Collectors.toSet())
                : Collections.emptySet();
        Set<Integer> volunteerIds = team.getVolunteers() != null
                ? team.getVolunteers().stream().map(User::getId).collect(Collectors.toSet())
                : Collections.emptySet();

        return TeamDTO.builder()
                .id(team.getId())
                .name(team.getName())
                .description(team.getDescription())
                .eventId(team.getEvent().getId())
                .responsibleId(team.getResponsible() != null ? team.getResponsible().getId() : null)
                .responsibleUsername(team.getResponsible() != null ? team.getResponsible().getEmail() : null)
                .taskIds(taskIds)
                .volunteerIds(volunteerIds)
                .build();
    }
}