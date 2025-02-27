package tn.ucar.enicar.info.projetspring.entities;

import java.util.Date;
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
    private Date startdate ;
    private Date enddate ;
    private String location ;

    @ManyToMany(mappedBy="events", cascade = CascadeType.ALL)
    private Set<User> users;

    @OneToMany(cascade = CascadeType.ALL, mappedBy="event")
    private Set<task> tasks;

    @OneToOne
    private discussion discussion;
}
