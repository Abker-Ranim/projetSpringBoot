package tn.ucar.enicar.info.projetspring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.ucar.enicar.info.projetspring.entities.RequestStatus;
import tn.ucar.enicar.info.projetspring.entities.RoleRequest;

import java.util.List;

public interface RoleRequestRepository extends JpaRepository<RoleRequest, Long> {
    List<RoleRequest> findByUserId(Integer userId);
    List<RoleRequest> findByStatus(RequestStatus status);
}