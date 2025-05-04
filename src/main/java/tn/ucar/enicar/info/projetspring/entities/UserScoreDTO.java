package tn.ucar.enicar.info.projetspring.entities;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserScoreDTO {
    private Integer id;
    private String name;
    private String email;
    private int score;
}