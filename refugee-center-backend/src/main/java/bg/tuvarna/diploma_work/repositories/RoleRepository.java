package bg.tuvarna.diploma_work.repositories;

import bg.tuvarna.diploma_work.enumerables.RoleType;
import bg.tuvarna.diploma_work.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    @Query("SELECT r FROM Role r WHERE r.roleType = ?1")
    Role getRole(RoleType roleType);
}
