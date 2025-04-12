package tn.ucar.enicar.info.projetspring.entities;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static tn.ucar.enicar.info.projetspring.entities.Permission.*;

@RequiredArgsConstructor
public enum Role {
    USER(Collections.emptySet()),
    ADMIN(Set.of(
            ADMIN_READ,
            ADMIN_UPDATE,
            ADMIN_DELETE,
            ADMIN_CREATE,
            VOLUNTARY_READ,
            VOLUNTARY_UPDATE,
            VOLUNTARY_DELETE,
            VOLUNTARY_CREATE
    )),
    VOLUNTARY(Set.of(
            VOLUNTARY_READ,
            VOLUNTARY_UPDATE,
            VOLUNTARY_DELETE,
            VOLUNTARY_CREATE
    )),
    RESPONSIBLE(Set.of(
            VOLUNTARY_READ,
            VOLUNTARY_UPDATE,
            VOLUNTARY_DELETE,
            VOLUNTARY_CREATE
            // Ajoutez d'autres permissions spécifiques si nécessaire
    ));

    @Getter
    private final Set<Permission> permissions;

    public List<SimpleGrantedAuthority> getAuthorities() {
        var authorities = getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }
}