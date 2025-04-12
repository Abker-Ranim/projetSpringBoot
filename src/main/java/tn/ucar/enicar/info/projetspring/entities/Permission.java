package tn.ucar.enicar.info.projetspring.entities;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Permission {
    ADMIN_READ("admin:read"),
    ADMIN_CREATE("admin:create"),
    ADMIN_UPDATE("admin:update"),
    ADMIN_DELETE("admin:delete"),
    VOLUNTARY_READ("management:read"),
    VOLUNTARY_CREATE("management:create"),
    VOLUNTARY_UPDATE("management:update"),
    VOLUNTARY_DELETE("management:delete");
    private final String permission;

}
