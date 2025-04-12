package tn.ucar.enicar.info.projetspring.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tn.ucar.enicar.info.projetspring.auth.RoleRequestDTO;
import tn.ucar.enicar.info.projetspring.auth.RoleRequestInputDTO;
import tn.ucar.enicar.info.projetspring.entities.Role;
import tn.ucar.enicar.info.projetspring.sevices.RoleRequestService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rolerequest")
@RequiredArgsConstructor
public class RoleRequestController {
    private final RoleRequestService roleRequestService;

    @PostMapping("/submit/{userId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<RoleRequestDTO> submitRoleRequest(
            @PathVariable Integer userId,
            @RequestBody RoleRequestInputDTO requestDTO) {
        return ResponseEntity.ok(
                roleRequestService.submitRoleRequest(userId, Role.valueOf(requestDTO.getRequestedRole()))
        );
    }

    @PutMapping("/approve/{requestId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoleRequestDTO> approveRoleRequest(
            @PathVariable Long requestId,
            @RequestBody String comments) {
        return ResponseEntity.ok(
                roleRequestService.approveRoleRequest(requestId, comments)
        );
    }

    @PutMapping("/reject/{requestId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoleRequestDTO> rejectRoleRequest(
            @PathVariable Long requestId,
            @RequestBody String comments) {
        return ResponseEntity.ok(
                roleRequestService.rejectRoleRequest(requestId, comments)
        );
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RoleRequestDTO>> getPendingRequests() {
        return ResponseEntity.ok(
                roleRequestService.getPendingRequests()
        );
    }
}