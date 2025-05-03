package tn.ucar.enicar.info.projetspring.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.ucar.enicar.info.projetspring.auth.RoleRequestDTO;
import tn.ucar.enicar.info.projetspring.auth.RoleRequestInputDTO;

import tn.ucar.enicar.info.projetspring.entities.User;
import tn.ucar.enicar.info.projetspring.repositories.RoleRequestRepository;
import tn.ucar.enicar.info.projetspring.sevices.RoleRequestService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/role-request")
@RequiredArgsConstructor

public class RoleRequestController {
    private final RoleRequestService roleRequestService;

    // Étape 2 : Utilisateur postule pour devenir RESPONSIBLE
    @PostMapping("/apply")
    public ResponseEntity<RoleRequestDTO> applyForRole(
            @RequestBody @Valid RoleRequestInputDTO request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(roleRequestService.createRoleRequest(request, request.getExperience(), request.getDescription(), user));
    }

    // Étape 3 : Admin approuve/rejette une demande de RESPONSIBLE
    @PutMapping("/{requestId}/review")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoleRequestDTO> reviewResponsibleRequest(
            @PathVariable Long requestId,
            @RequestParam String status,
            @RequestParam(required = false) String comments) {
        return ResponseEntity.ok(roleRequestService.reviewRoleRequest(requestId, status, comments));
    }
// apply voluntary
    @PostMapping("/apply-volunteer")
    public ResponseEntity<RoleRequestDTO> applyForVolunteer(
            @RequestPart("request") @Valid RoleRequestInputDTO request,
            @RequestPart("cv") MultipartFile cv,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(roleRequestService.createVolunteerRequest(request, cv, user));
    }
    //accecpt ou reject responsible
    @PutMapping("/{requestId}/review-volunteer")
    public ResponseEntity<RoleRequestDTO> reviewVolunteerRequest(
            @PathVariable Long requestId,
            @RequestParam String status,
            @RequestParam(required = false) String comments)
            {
        return ResponseEntity.ok(roleRequestService.reviewVolunteerRequest(requestId, status, comments));
    }

    // Liste des demandes en attente (RESPONSIBLE)
    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESPONSIBLE')")
    public ResponseEntity<List<RoleRequestDTO>> getPendingRequests(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(roleRequestService.getPendingRequests(user));
    }
    // Liste des candidatures en attente pour un utilisateur spécifique
    @GetMapping("/pending-by-user")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<RoleRequestDTO>> getPendingRequestsByUser(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(roleRequestService.getPendingRequestsByUser(user));
    }

    // Liste des candidatures VOLUNTARY pour les tâches d'un responsable
    @GetMapping("/volunteer-by-responsible")
    public ResponseEntity<List<RoleRequestDTO>> getVolunteerRequestsByResponsible(
            @AuthenticationPrincipal User responsible) {
        return ResponseEntity.ok(roleRequestService.getVolunteerRequestsByResponsible(responsible));
    }
    // New endpoint to fetch RoleRequest by ID
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RoleRequestDTO> getRoleRequestById(@PathVariable Long id) {
        RoleRequestDTO roleRequestDTO = roleRequestService.getRoleRequestById(id);
        return ResponseEntity.ok(roleRequestDTO);
    }

    // New endpoint: Get team responsible requests for a specific user
    @GetMapping("/team-responsible-by-user")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<RoleRequestDTO>> getTeamResponsibleRequestsByUser(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(roleRequestService.getTeamResponsibleRequestsByUser(user));
    }

    // New endpoint: Get all team responsible requests
    @GetMapping("/team-responsible-all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RoleRequestDTO>> getAllTeamResponsibleRequests() {
        return ResponseEntity.ok(roleRequestService.getAllTeamResponsibleRequests());
    }
}