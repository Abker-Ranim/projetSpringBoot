package tn.ucar.enicar.info.projetspring.entities;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LeaderboardEntryDTO {
    private String email;
    private Integer score;
    private Integer rank;
}
