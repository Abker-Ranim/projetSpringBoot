package tn.ucar.enicar.info.projetspring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.ucar.enicar.info.projetspring.entities.Candidature;

public interface CandidatureRepository extends JpaRepository<Candidature, Long> {
}