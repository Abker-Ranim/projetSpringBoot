package tn.ucar.enicar.info.projetspring.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

    public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String firstname;
    private String lastname;
    private String email;
    @JsonIgnore
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;
    private Integer score ;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Token>tokens;

    // Événements auxquels l'utilisateur participe
    @ManyToMany(cascade = CascadeType.ALL )
    @JoinTable(
            name = "user_event",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    private Set<event> events;
    // Événements dont l'utilisateur est responsable
    @ManyToMany(mappedBy = "responsibles", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<event> responsibleEvents;

    // Tâches créées par l'utilisateur (en tant que responsable)
    @OneToMany(mappedBy = "responsible", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<task> createdTasks;

    // Tâches auxquelles l'utilisateur est volontaire
    @ManyToMany(mappedBy = "volunteers", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<task> volunteerTasks;

    @OneToMany(cascade = CascadeType.ALL, mappedBy="user")
    private Set<comment> comments;

    @ManyToMany(cascade = CascadeType.ALL)
    private Set<notification> notifications;


    @OneToMany(mappedBy = "responsible")
    @JsonIgnore
    private Set<Team> responsibleTeams;

    @ManyToMany(mappedBy = "volunteers")
    @JsonIgnore
    private Set<Team> voluntaryTeams;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
