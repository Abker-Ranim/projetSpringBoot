package tn.ucar.enicar.info.projetspring.entities;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Permission {
    ADMIN_READ("admin:read"),
    ADMIN_UPDATE("admin:update"),
    ADMIN_CREATE("admin:create"),
    ADMIN_DELETE("admin:delete"),
    RESPONSIBLE_READ("responsible:read"),
    RESPONSIBLE_UPDATE("responsible:update"),
    RESPONSIBLE_CREATE("responsible:create"),
    RESPONSIBLE_DELETE("responsible:delete"),
    VOLUNTARY_READ("voluntary:read"),
    VOLUNTARY_UPDATE("voluntary:update"),
    VOLUNTARY_CREATE("voluntary:create"),
    VOLUNTARY_DELETE("voluntary:delete");
    private final String permission;

}
