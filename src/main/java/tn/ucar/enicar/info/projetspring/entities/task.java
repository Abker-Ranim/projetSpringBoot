package tn.ucar.enicar.info.projetspring.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.util.*;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

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
    private Integer note ;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false)
    private Date completedAt;


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


    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<comment> comments = new ArrayList<>();



}
