package bg.tuvarna.diploma_work.repositories;

import bg.tuvarna.diploma_work.models.Donation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Long> {

    @Query("SELECT d FROM Donation d WHERE d.donationType = 0")
    Donation getMoneyDonation();
}
