package tn.ucar.enicar.info.projetspring.entities;

import lombok.*;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamDTO {
    private Long id;
    private String name;
    private String description;
    private Long eventId;
    private Integer responsibleId;
    private String responsibleUsername;
    private Set<Long> taskIds;
    private Set<Integer> volunteerIds;
}