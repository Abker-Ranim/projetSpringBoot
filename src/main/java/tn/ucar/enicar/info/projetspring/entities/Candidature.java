package tn.ucar.enicar.info.projetspring.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Candidature implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String description; // Motivation ou détails de la candidature

    private String cvPath; // Chemin ou URL du CV (ex. "/uploads/cvs/123.pdf")

    private LocalDateTime submittedAt; // Date de soumission

    @OneToOne(mappedBy = "candidature")
    @JsonIgnore
    private RoleRequest roleRequest; // Lien avec la demande de rôle
}