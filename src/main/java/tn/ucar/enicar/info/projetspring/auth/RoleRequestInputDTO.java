package tn.ucar.enicar.info.projetspring.auth;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleRequestInputDTO {
    private String requestedRole;
    private Long eventId;
    private Long taskId;
    private Long teamId;
    private String description;
}
