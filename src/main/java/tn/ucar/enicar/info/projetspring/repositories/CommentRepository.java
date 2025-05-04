package tn.ucar.enicar.info.projetspring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.ucar.enicar.info.projetspring.entities.comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<comment, Long> {
    List<comment> findByTaskId(Long taskId);
}