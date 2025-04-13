package tn.ucar.enicar.info.projetspring.sevices;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.ucar.enicar.info.projetspring.entities.*;
import tn.ucar.enicar.info.projetspring.repositories.TaskRepository;
import tn.ucar.enicar.info.projetspring.repositories.TeamRepository;
import tn.ucar.enicar.info.projetspring.repositories.userRepo;
import tn.ucar.enicar.info.projetspring.sevices.EventService;

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
                .eventId(team.getEvent().getId())
                .responsibleId(team.getResponsible() != null ? team.getResponsible().getId() : null)
                .responsibleUsername(team.getResponsible() != null ? team.getResponsible().getEmail() : null)
                .taskIds(taskIds)
                .volunteerIds(volunteerIds)
                .build();
    }

}