package tn.ucar.enicar.info.projetspring.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tn.ucar.enicar.info.projetspring.auth.RoleRequestDTO;
import tn.ucar.enicar.info.projetspring.auth.RoleRequestInputDTO;
import tn.ucar.enicar.info.projetspring.entities.User;
import tn.ucar.enicar.info.projetspring.sevices.RoleRequestService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/role-request")
@RequiredArgsConstructor
public class RoleRequestController {
    private final RoleRequestService roleRequestService;

    // Étape 2 : Utilisateur postule pour devenir RESPONSIBLE
    @PostMapping("/apply")
    public ResponseEntity<RoleRequestDTO> applyForRole(
            @RequestBody RoleRequestInputDTO request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(roleRequestService.createRoleRequest(request, user));
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

    // Étape 5 : Utilisateur postule pour devenir VOLUNTARY
    @PostMapping("/apply-volunteer")
    public ResponseEntity<RoleRequestDTO> applyForVolunteer(
            @RequestBody RoleRequestInputDTO request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(roleRequestService.createVolunteerRequest(request, user));
    }

    // Étape 6 : Responsible approuve/rejette une demande de VOLUNTARY
    @PutMapping("/{requestId}/review-volunteer")
    @PreAuthorize("hasRole('RESPONSIBLE')")
    public ResponseEntity<RoleRequestDTO> reviewVolunteerRequest(
            @PathVariable Long requestId,
            @RequestParam String status,
            @RequestParam(required = false) String comments,
            @AuthenticationPrincipal User responsible) {
        return ResponseEntity.ok(roleRequestService.reviewVolunteerRequest(requestId, status, comments, responsible));
    }

    // Liste des demandes en attente (pour admin ou responsible)
    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESPONSIBLE')")
    public ResponseEntity<List<RoleRequestDTO>> getPendingRequests(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(roleRequestService.getPendingRequests(user));
    }
}