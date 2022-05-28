package bg.tuvarna.diploma_work.repositories;

import bg.tuvarna.diploma_work.models.Donor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DonorRepository extends JpaRepository<Donor, Long> {

    @Query("SELECT d FROM Donor d WHERE d.email = ?1")
    Donor getDonor(String email);
}
