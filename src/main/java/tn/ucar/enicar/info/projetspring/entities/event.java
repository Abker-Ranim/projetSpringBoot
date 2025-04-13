package tn.ucar.enicar.info.projetspring.entities;

import java.util.ArrayList;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class event implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String title;
    private String description;
    private Date startDate ;
    private Date endDate ;
    private String location ;

    @ManyToMany(mappedBy="events", cascade = CascadeType.ALL)
    private Set<User> users;
    // Responsables de l'événement
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "event_responsible",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @JsonIgnore
    private Set<User> responsibles;


    @OneToMany(cascade = CascadeType.ALL, mappedBy="event")
    @JsonIgnore
    private Set<task> tasks;

    @OneToOne
    private discussion discussion;
}
