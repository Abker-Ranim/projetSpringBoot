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
    private String eventTitle; // Changed from eventId
    private String taskTitle;  // Changed from taskId
    private String teamName;
    private Long teamId;
    private String experience;
    private String userName; // Pour VOLUNTARY
    private String description; // Pour VOLUNTARY
    private String cvPath; // Pour VOLUNTARY
    private LocalDateTime submittedAt; // Pour VOLUNTARY
}
