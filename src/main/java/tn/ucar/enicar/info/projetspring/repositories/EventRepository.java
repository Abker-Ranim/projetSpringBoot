package tn.ucar.enicar.info.projetspring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.ucar.enicar.info.projetspring.entities.event;

public interface EventRepository extends JpaRepository<event, Long> {
}