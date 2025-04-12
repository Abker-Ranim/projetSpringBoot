package tn.ucar.enicar.info.projetspring.sevices;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import tn.ucar.enicar.info.projetspring.auth.RoleRequestDTO;
import tn.ucar.enicar.info.projetspring.entities.*;
import tn.ucar.enicar.info.projetspring.repositories.RoleRequestRepository;
import tn.ucar.enicar.info.projetspring.repositories.userRepo;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleRequestService {
    private final RoleRequestRepository roleRequestRepository;
    private final userRepo userRepository;

    public RoleRequestDTO submitRoleRequest(Integer userId, Role requestedRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getRole() != Role.USER) {
            throw new IllegalStateException("Only USER can submit role requests");
        }

        if (requestedRole != Role.VOLUNTARY && requestedRole != Role.RESPONSIBLE) {
            throw new IllegalArgumentException("Invalid role requested");
        }

        RoleRequest request = RoleRequest.builder()
                .user(user)
                .requestedRole(requestedRole)
                .status(RequestStatus.PENDING)
                .build();

        RoleRequest savedRequest = roleRequestRepository.save(request);

        return RoleRequestDTO.builder()
                .id(savedRequest.getId())
                .requestedRole(savedRequest.getRequestedRole().name())
                .status(savedRequest.getStatus().name())
                .comments(savedRequest.getComments())
                .userEmail(user.getEmail())
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public RoleRequestDTO approveRoleRequest(Long requestId, String comments) {
        RoleRequest request = roleRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException("Request is not pending");
        }

        request.setStatus(RequestStatus.APPROVED);
        request.setComments(comments);

        User user = request.getUser();
        user.setRole(request.getRequestedRole());
        userRepository.save(user);

        RoleRequest savedRequest = roleRequestRepository.save(request);

        return RoleRequestDTO.builder()
                .id(savedRequest.getId())
                .requestedRole(savedRequest.getRequestedRole().name())
                .status(savedRequest.getStatus().name())
                .comments(savedRequest.getComments())
                .userEmail(user.getEmail())
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public RoleRequestDTO rejectRoleRequest(Long requestId, String comments) {
        RoleRequest request = roleRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException("Request is not pending");
        }

        request.setStatus(RequestStatus.REJECTED);
        request.setComments(comments);

        RoleRequest savedRequest = roleRequestRepository.save(request);

        return RoleRequestDTO.builder()
                .id(savedRequest.getId())
                .requestedRole(savedRequest.getRequestedRole().name())
                .status(savedRequest.getStatus().name())
                .comments(savedRequest.getComments())
                .userEmail(savedRequest.getUser().getEmail())
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<RoleRequestDTO> getPendingRequests() {
        return roleRequestRepository.findByStatus(RequestStatus.PENDING)
                .stream()
                .map(request -> RoleRequestDTO.builder()
                        .id(request.getId())
                        .requestedRole(request.getRequestedRole().name())
                        .status(request.getStatus().name())
                        .comments(request.getComments())
                        .userEmail(request.getUser().getEmail())
                        .build())
                .collect(Collectors.toList());
    }
}