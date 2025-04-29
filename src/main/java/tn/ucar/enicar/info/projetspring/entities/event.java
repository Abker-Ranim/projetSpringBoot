package tn.ucar.enicar.info.projetspring.entities;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(message = "Le titre est obligatoire")
    private String title;

    @NotBlank(message = "La description est obligatoire")
    private String description;

    @Temporal(TemporalType.TIMESTAMP)
    @Future(message = "La date doit être dans le futur")
    private Date startDate ;

    @Temporal(TemporalType.TIMESTAMP)
    @Future(message = "La date doit être dans le futur")
    private Date endDate ;

    private Integer participants;

    @NotBlank(message = "Le lieu est obligatoire")
    private String location ;

    private String organization;
    private String vision;
    private String imagePath;


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
