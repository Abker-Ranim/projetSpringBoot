package tn.ucar.enicar.info.projetspring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.ucar.enicar.info.projetspring.entities.Role;
import tn.ucar.enicar.info.projetspring.entities.User;

import java.util.List;
import java.util.Optional;

public interface userRepo extends JpaRepository<User, Integer> {
Optional<User> findByEmail(String email);
    List<User> findByRole(Role role);

}
