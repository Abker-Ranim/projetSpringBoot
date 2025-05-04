package tn.ucar.enicar.info.projetspring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.ucar.enicar.info.projetspring.entities.task;
import tn.ucar.enicar.info.projetspring.entities.User;

import java.util.List;

public interface TaskRepository extends JpaRepository<task, Long> {
    List<task> findByEventId(Long eventId);
    List<task> findByResponsible(User responsible);
    List<task> findByVolunteersContaining(User user);
}