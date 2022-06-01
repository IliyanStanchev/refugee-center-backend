package bg.tuvarna.diploma_work.repositories;

import bg.tuvarna.diploma_work.models.Donation;
import bg.tuvarna.diploma_work.models.DonationAbsorption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DonationAbsorptionRepository extends JpaRepository<DonationAbsorption, Long>  {

    @Query("SELECT d FROM DonationAbsorption d WHERE d.facility.id = ?1")
    List<DonationAbsorption> getDonationAbsorptions(Long shelterId);

    @Query("SELECT d FROM DonationAbsorption d WHERE d.donation.id = ?1")
    List<DonationAbsorption> getAbsorptions(Long donationId);
}
