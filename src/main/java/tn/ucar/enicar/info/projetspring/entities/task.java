package tn.ucar.enicar.info.projetspring.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.util.Date;
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
@Builder

public class task implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String title ;
    private String description ;
    private Date deadline ;
    @Enumerated(EnumType.STRING)
    private status Status ;
    private int note ;

    // Responsable qui a créé la tâche
    @ManyToOne
    @JsonIgnore
    private User responsible;

    @ManyToOne
    @JsonIgnore
    event event ;

    @ManyToOne
    @JsonIgnore
    private Team team;
    // Volontaires assignés à la tâche
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "task_volunteer",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @JsonIgnore
    private Set<User> volunteers = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy="task")
    @JsonIgnore
    private Set<comment> comments;


}
