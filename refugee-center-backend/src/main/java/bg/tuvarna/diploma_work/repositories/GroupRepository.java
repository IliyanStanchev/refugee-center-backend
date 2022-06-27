package bg.tuvarna.diploma_work.repositories;

import bg.tuvarna.diploma_work.models.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GroupRepository extends JpaRepository<Group, Long> {

    @Query("SELECT g FROM Group g WHERE g.email = ?1")
    Group getByEmail(String email);

}
