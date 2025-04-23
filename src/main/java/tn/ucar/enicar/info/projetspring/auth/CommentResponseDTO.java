package tn.ucar.enicar.info.projetspring.auth;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDTO {
    private Long id;
    private String content;
    private Long taskId;
    private String userEmail;
    private LocalDateTime createdAt;
}