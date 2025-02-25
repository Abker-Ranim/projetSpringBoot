package tn.ucar.enicar.info.projetspring.entities;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

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

    @ManyToOne
    user user;

    @ManyToOne
    event event ;

    @OneToMany(cascade = CascadeType.ALL, mappedBy="task")
    private Set<comment> comments;
}
