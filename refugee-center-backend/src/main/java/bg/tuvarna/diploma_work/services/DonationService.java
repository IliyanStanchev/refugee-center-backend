package bg.tuvarna.diploma_work.services;

import bg.tuvarna.diploma_work.models.Donation;
import bg.tuvarna.diploma_work.models.Donor;
import bg.tuvarna.diploma_work.repositories.DonationRepository;
import bg.tuvarna.diploma_work.repositories.DonorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DonationService {

    @Autowired
    private DonationRepository donationRepository;

    @Autowired
    private DonorRepository donorRepository;

    public Donation saveDonation(Donation donation) {
        return donationRepository.save(donation);
    }

    public List<Donation> getDonations() {

        return donationRepository.findAll();
    }

    public Donation getMoneyDonation() {

        return donationRepository.getMoneyDonation();
    }

    public Donation getDonation(Long donationId) {

        Optional<Donation> donationOptional = donationRepository.findById(donationId);
        if (donationOptional.isPresent())
            return donationOptional.get();

        return null;
    }

    public Donor getDonor(String email) {

        return donorRepository.getDonor(email);
    }

    public Donor saveDonor(Donor donor) {

        return donorRepository.save(donor);
    }

    public List<Donation> getAllDonations() {

        return donationRepository.findAll();
    }

    public List<Donor> getDonors() {

        return donorRepository.getTopDonors();
    }

    public void reserveDonations(long threadId) {

        donationRepository.reserveDonations(threadId);
    }

    public List<Donation> getReservedDonations(long threadId) {

        return donationRepository.getReservedDonations(threadId);
    }
}