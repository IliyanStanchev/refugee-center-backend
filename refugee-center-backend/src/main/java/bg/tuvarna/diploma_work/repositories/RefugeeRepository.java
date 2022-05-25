package bg.tuvarna.diploma_work.repositories;

import bg.tuvarna.diploma_work.models.Refugee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RefugeeRepository extends JpaRepository<Refugee, Long> {

    @Query("SELECT r FROM Refugee r WHERE r.user.id = ?1")
    Refugee getRefugeeByUserID(Long userID);

    @Query("SELECT r FROM Refugee r WHERE r.user.accountStatus.accountStatusType = 0")
    List<Refugee> getPendingRegistrations();
}