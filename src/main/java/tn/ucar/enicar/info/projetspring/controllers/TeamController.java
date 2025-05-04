package tn.ucar.enicar.info.projetspring.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tn.ucar.enicar.info.projetspring.entities.Team;
import tn.ucar.enicar.info.projetspring.entities.TeamDTO;
import tn.ucar.enicar.info.projetspring.sevices.TeamService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/team")
@RequiredArgsConstructor
public class TeamController {
    private final TeamService teamService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TeamDTO> createTeam(@RequestBody @Valid Team team, @RequestParam Long eventId) {
        return ResponseEntity.ok(teamService.createTeam(team, eventId));
    }

    @PutMapping("/{teamId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TeamDTO> updateTeam(
            @PathVariable Long teamId,
            @RequestBody @Valid Team team,
            @RequestParam(required = false) Long eventId) {
        return ResponseEntity.ok(teamService.updateTeam(teamId, team, eventId));
    }

    @DeleteMapping("/{teamId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long teamId) {
        teamService.deleteTeam(teamId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/event/{eventId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<TeamDTO>> getTeamsByEventId(@PathVariable Long eventId) {
        return ResponseEntity.ok(teamService.getTeamsByEventId(eventId));
    }
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RESPONSIBLE')")
    public ResponseEntity<List<TeamDTO>> getAllTeams() {
        return ResponseEntity.ok(teamService.getAllTeams());
    }
}