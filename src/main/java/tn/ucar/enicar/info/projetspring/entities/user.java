package tn.ucar.enicar.info.projetspring.entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

    public class user  implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String Firstname;
    private String Lastname;
    private String Email;
    private String Password;
    @Enumerated(EnumType.STRING)
    private role Role;

    @ManyToMany(cascade = CascadeType.ALL)
    private Set<event> events;

    @OneToMany(cascade = CascadeType.ALL, mappedBy="user")
    private Set<task> tasks;

    @OneToMany(cascade = CascadeType.ALL, mappedBy="user")
    private Set<comment> comments;

    @ManyToMany(cascade = CascadeType.ALL)
    private Set<notification> notifications;

    @OneToMany(cascade = CascadeType.ALL, mappedBy="user")
    private Set<message> messages;
}
