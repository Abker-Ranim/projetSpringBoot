package tn.ucar.enicar.info.projetspring.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
@Builder
public class Team implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom de l'équipe est obligatoire")
    private String name;

    @NotBlank(message = "La description de l'équipe est obligatoire")
    private String description;

    @ManyToOne
    @JsonIgnore
    private User responsible;

    @ManyToOne
    @JsonIgnore
    private event event;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<task> tasks = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "team_volunteer",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @JsonIgnore
    private Set<User> volunteers = new HashSet<>();
}