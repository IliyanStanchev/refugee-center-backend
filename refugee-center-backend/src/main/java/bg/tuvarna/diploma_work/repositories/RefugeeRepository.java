package bg.tuvarna.diploma_work.repositories;

import bg.tuvarna.diploma_work.models.Refugee;
import bg.tuvarna.diploma_work.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Ref;
import java.util.List;

@Repository
public interface RefugeeRepository extends JpaRepository<Refugee, Long> {

    @Query("SELECT r FROM Refugee r WHERE r.user.id = ?1")
    Refugee getRefugeeByUserID(Long userID);

    @Query("SELECT r FROM Refugee r WHERE r.user.accountStatus.accountStatusType = 0")
    List<Refugee> getPendingRegistrations();

    @Query("SELECT r FROM Refugee r WHERE r.facility.id = ?1 and r.removedFromFacility = false")
    List<Refugee> getRefugeesInShelter(Long shelterId);

    @Modifying
    @Query(value = "UPDATE Refugee r SET r.removedFromFacility = true WHERE r.id = ?1")
    void removeRefugeeFromShelter(Long refugeeId);

    @Query("SELECT r FROM Refugee r WHERE r.removedFromFacility = true")
    List<Refugee> getUsersWithoutShelter();

    @Modifying
    @Query(value="UPDATE REFUGEES SET REMOVED_FROM_FACILITY = FALSE, FACILITY_ID = ?1 WHERE ID = ?2", nativeQuery = true)
    void addRefugeeToShelter(Long shelterId, Long refugeeId);
}