package bg.tuvarna.diploma_work.repositories;

import bg.tuvarna.diploma_work.models.Donor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DonorRepository extends JpaRepository<Donor, Long> {

    @Query("SELECT d FROM Donor d WHERE d.email = ?1")
    Donor getDonor(String email);

    @Query(value = "SELECT d.* FROM donors d ORDER BY d.date_of_donation DESC LIMIT 10", nativeQuery = true)
    List<Donor> getTopDonors();
}
