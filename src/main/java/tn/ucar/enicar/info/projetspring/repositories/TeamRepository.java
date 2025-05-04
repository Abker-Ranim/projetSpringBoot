package tn.ucar.enicar.info.projetspring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.ucar.enicar.info.projetspring.entities.Team;
import tn.ucar.enicar.info.projetspring.entities.event;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findByEvent(event event);
}