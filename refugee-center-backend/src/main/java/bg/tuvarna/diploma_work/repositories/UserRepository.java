package bg.tuvarna.diploma_work.repositories;

import bg.tuvarna.diploma_work.enumerables.RoleType;
import bg.tuvarna.diploma_work.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.email = ?1")
    User getUserByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.identifier = ?1")
    User getUserByIdentifier(String identifier);

    @Query("SELECT u FROM User u WHERE u.role.roleType = ?1")
    List<User> getUsersByRole(RoleType roleType);

    @Query("SELECT u FROM User u WHERE u.role.roleType = 0 or u.role.roleType = 1 order by u.role.roleType")
    List<User> getResponsibleUsers();
}
