package tn.ucar.enicar.info.projetspring.entities;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CandidatureDTO {
    private Long id;
    private Long taskId;
    private String description;
    private String cvPath;
    private String status;
    private String comments;
    private LocalDateTime submittedAt;
    private Integer userId;
    private String userEmail;
    private Long teamId;
    private Long eventId;
}