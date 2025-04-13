package tn.ucar.enicar.info.projetspring.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Team implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    @ManyToOne
    @JsonIgnore
    private User responsible; // Le responsable de l'équipe

    @ManyToOne
    @JsonIgnore
    private event event; // L'événement auquel l'équipe est rattachée

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<task> tasks = new HashSet<>(); // Tâches associées à l'équipe

    @ManyToMany
    @JoinTable(
            name = "team_volunteer",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @JsonIgnore
    private Set<User> volunteers = new HashSet<>(); // Volontaires approuvés dans l'équipe
}