package tn.ucar.enicar.info.projetspring.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.util.Date;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private Date deadline;
    private status Status;
    private int note ;
    private Long eventId;
    private Long teamId;
    private Integer responsibleId;
    private String responsibleUsername;
    private Set<Integer> volunteerIds;


}

