package tn.ucar.enicar.info.projetspring.auth;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleRequestDTO {
    private Long id;
    private String requestedRole;
    private String status;
    private String comments;
    private String userEmail;
    private Long eventId;
    private Long taskId;
    private Long teamId;
    private String description; // Pour VOLUNTARY
    private String cvPath; // Pour VOLUNTARY
    private LocalDateTime submittedAt; // Pour VOLUNTARY
}
