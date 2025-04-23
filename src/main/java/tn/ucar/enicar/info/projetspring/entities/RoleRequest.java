package tn.ucar.enicar.info.projetspring.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class RoleRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Role requestedRole; // Utilise l'enum Role

    @Enumerated(EnumType.STRING)
    private RequestStatus status; // Utilise l'enum RequestStatus

    private String comments;

    @ManyToOne
    @JsonIgnore
    private User user;

    @ManyToOne
    @JsonIgnore
    private task task;

    @ManyToOne
    @JsonIgnore
    private Team team;


    @ManyToOne
    @JsonIgnore
    private event event;

    @OneToOne
    @JoinColumn(name = "candidature_id")
    @JsonIgnore
    private Candidature candidature; // Nouvelle relation pour VOLUNTARY
}