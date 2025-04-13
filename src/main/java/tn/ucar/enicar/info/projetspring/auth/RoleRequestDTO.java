package tn.ucar.enicar.info.projetspring.auth;

import lombok.*;

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
}
