package bg.tuvarna.diploma_work.repositories;

import bg.tuvarna.diploma_work.enumerables.RoleType;
import bg.tuvarna.diploma_work.models.Facility;
import bg.tuvarna.diploma_work.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FacilityRepository extends JpaRepository<Facility, Long> {

    @Query("SELECT f FROM Facility f WHERE f.facilityType = 1 AND f.currentCapacity != f.maxCapacity")
    List<Facility> getAllShelters();

    @Query("SELECT f FROM Facility f WHERE f.facilityType = 1 AND f.currentCapacity != f.maxCapacity AND f.id != ?1")
    List<Facility> getSheltersForTransfer(Long id);
}
