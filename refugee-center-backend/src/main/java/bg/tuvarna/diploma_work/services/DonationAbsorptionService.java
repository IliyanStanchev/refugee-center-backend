package bg.tuvarna.diploma_work.services;

import bg.tuvarna.diploma_work.models.Donation;
import bg.tuvarna.diploma_work.models.DonationAbsorption;
import bg.tuvarna.diploma_work.repositories.DonationAbsorptionRepository;
import bg.tuvarna.diploma_work.repositories.DonationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DonationAbsorptionService {

    @Autowired
    private DonationAbsorptionRepository donationAbsorptionRepository;

    @Autowired
    private DonationRepository donationRepository;

    public List<DonationAbsorption> getDonationAbsorptions(Long shelterId){
        return donationAbsorptionRepository.getDonationAbsorptions(shelterId);
    }

    public List<Donation> getNewShelterAbsorptions(Long shelterId) {
        return donationRepository.getNewShelterAbsorptions(shelterId);
    }

    public DonationAbsorption saveDonationAbsorption(DonationAbsorption donationAbsorption) {
        return donationAbsorptionRepository.save(donationAbsorption);
    }
}
