package bg.tuvarna.diploma_work.repositories;

import bg.tuvarna.diploma_work.models.Refugee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RefugeeRepository extends JpaRepository<Refugee, Long> {

    @Query("SELECT r FROM Refugee r WHERE r.user.id = ?1")
    Refugee getRefugeeByUserID(Long userID);
}