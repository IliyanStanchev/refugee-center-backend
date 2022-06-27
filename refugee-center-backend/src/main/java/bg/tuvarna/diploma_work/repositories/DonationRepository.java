package bg.tuvarna.diploma_work.repositories;

import bg.tuvarna.diploma_work.models.Donation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Long> {

    @Query("SELECT d FROM Donation d WHERE d.donationType = 0")
    Donation getMoneyDonation();

    @Query("SELECT d FROM Donation d WHERE d.id NOT IN ( SELECT da.donation.id FROM DonationAbsorption da WHERE da.facility.id = ?1 )")
    List<Donation> getNewShelterAbsorptions(Long shelterId);


    @Modifying
    @Query(value = "UPDATE donations m SET thread_id = ?1 WHERE id IN ( SELECT id FROM donations WHERE thread_id = 0 LIMIT 100 )", nativeQuery = true)
    void reserveDonations(long threadId);

    @Query("SELECT d FROM Donation d WHERE d.threadId = ?1")
    List<Donation> getReservedDonations(long threadId);
}
