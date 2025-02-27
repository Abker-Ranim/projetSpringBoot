package tn.ucar.enicar.info.projetspring.sevices;

import tn.ucar.enicar.info.projetspring.entities.User;

import java.util.List;

public interface userService {
    public List<User> retrieveAllUsers();
}
