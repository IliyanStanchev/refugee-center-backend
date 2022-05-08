package bg.tuvarna.diploma_work.services;

import bg.tuvarna.diploma_work.models.Donation;
import bg.tuvarna.diploma_work.repositories.DonationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DonationService {

    @Autowired
    private DonationRepository donationRepository;

    public Donation saveDonation(Donation donation) {
        return donationRepository.save(donation);
    }
}