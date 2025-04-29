package tn.ucar.enicar.info.projetspring.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleRequestInputDTO {
    @NotBlank(message = "Requested role is required")
    private String requestedRole;
    @NotNull(message = "Event ID is required for RESPONSIBLE role")
    private Long eventId;
    private Long taskId;
    @NotNull(message = "Team ID is required for RESPONSIBLE role")
    private Long teamId;
    private String description;
    private String experience;
}