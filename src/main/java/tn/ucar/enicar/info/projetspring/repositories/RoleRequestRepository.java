package tn.ucar.enicar.info.projetspring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.ucar.enicar.info.projetspring.entities.RequestStatus;
import tn.ucar.enicar.info.projetspring.entities.Role;
import tn.ucar.enicar.info.projetspring.entities.RoleRequest;
import tn.ucar.enicar.info.projetspring.entities.User;

import java.util.List;

public interface RoleRequestRepository extends JpaRepository<RoleRequest, Long> {
    List<RoleRequest> findByStatus(RequestStatus status);
    List<RoleRequest> findByStatusAndTaskIdIn(RequestStatus status, List<Long> taskIds);
    List<RoleRequest> findByUser(User user);
    List<RoleRequest> findByRequestedRoleAndTaskIdIn(Role requestedRole, List<Long> taskIds);
}