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
import tn.ucar.enicar.info.projetspring.entities.CandidatureDTO;
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

    @PostMapping("/apply-volunteer")
    public ResponseEntity<RoleRequestDTO> applyForVolunteer(
            @RequestPart("request") @Valid RoleRequestInputDTO request,
            @RequestPart("cv") MultipartFile cv,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(roleRequestService.createVolunteerRequest(request, cv, user));
    }
    @PutMapping("/{requestId}/review-volunteer")
    @PreAuthorize("hasRole('RESPONSIBLE')")
    public ResponseEntity<RoleRequestDTO> reviewVolunteerRequest(
            @PathVariable Long requestId,
            @RequestParam String status,
            @RequestParam(required = false) String comments)
            {
        return ResponseEntity.ok(roleRequestService.reviewVolunteerRequest(requestId, status, comments));
    }
    // Liste des candidatures VOLUNTARY en attente
    @GetMapping("/pending-volunteer")
    @PreAuthorize("hasRole('RESPONSIBLE')")
    public ResponseEntity<List<RoleRequestDTO>> getPendingVolunteerCandidatures(
            @AuthenticationPrincipal User responsible) {
        return ResponseEntity.ok(roleRequestService.getPendingVolunteerCandidatures(responsible));
    }

    // Liste des demandes en attente (RESPONSIBLE)
    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESPONSIBLE')")
    public ResponseEntity<List<RoleRequestDTO>> getPendingRequests(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(roleRequestService.getPendingRequests(user));
    }

}