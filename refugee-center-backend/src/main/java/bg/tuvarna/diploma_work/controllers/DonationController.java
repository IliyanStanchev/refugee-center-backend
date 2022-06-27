package bg.tuvarna.diploma_work.controllers;

import bg.tuvarna.diploma_work.enumerables.DonationType;
import bg.tuvarna.diploma_work.enumerables.Unit;
import bg.tuvarna.diploma_work.exceptions.InternalErrorResponseStatusException;
import bg.tuvarna.diploma_work.models.Donation;
import bg.tuvarna.diploma_work.models.Donor;
import bg.tuvarna.diploma_work.services.DonationService;
import bg.tuvarna.diploma_work.services.LogService;
import bg.tuvarna.diploma_work.storages.DonationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@CrossOrigin(origins = {"http://localhost:3000"})
@RestController
public class DonationController {

    @Autowired
    private DonationService donationService;

    @Autowired
    private LogService logService;

    @PostMapping("/donate-money")
    public ResponseEntity<Void> donateMoney(@RequestBody Donation donation) {

        Donation moneyDonation = donationService.getMoneyDonation();
        if (moneyDonation == null) {
            moneyDonation.setDonationType(DonationType.Money);
            moneyDonation.setQuantity(0);
            moneyDonation.setUnit(Unit.USD);
            moneyDonation.setName("Money donation");

            moneyDonation = donationService.saveDonation(moneyDonation);
        }

        moneyDonation.setQuantity(moneyDonation.getQuantity() + donation.getQuantity());
        moneyDonation = donationService.saveDonation(moneyDonation);

        if (moneyDonation == null) {
            logService.logErrorMessage("DonationService::saveDonation", String.valueOf(donation.getQuantity()) + donation.getUnit());
            return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @GetMapping("/get-donations")
    public List<Donation> getDonations() {

        return donationService.getDonations();
    }

    @PostMapping("/save-donation")
    @Transactional
    public ResponseEntity<Void> saveDonation(@RequestBody DonationData donationData) {

        Donation databaseDonation = donationService.getDonation(donationData.getDonation().getId());

        if (databaseDonation != null) {
            databaseDonation.setQuantity(donationData.getDonation().getQuantity() + databaseDonation.getQuantity());
        }

        Donation newDonation = databaseDonation == null ? donationData.getDonation() : databaseDonation;

        newDonation = donationService.saveDonation(newDonation);
        if (newDonation == null) {

            logService.logErrorMessage("DonationService::saveDonation", String.valueOf(newDonation.getQuantity()) + newDonation.getUnit());
            throw new InternalErrorResponseStatusException();
        }

        if (donationData.getDonor().getEmail().isBlank())
            return new ResponseEntity<Void>(HttpStatus.OK);

        Donor donor = donationService.getDonor(donationData.getDonor().getEmail());

        Donor newDonor = donor == null ? donationData.getDonor() : donor;
        newDonor.setDateOfDonation(LocalDate.now());

        newDonor = donationService.saveDonor(newDonor);

        if (newDonor == null) {
            logService.logErrorMessage("DonationService::saveDonor", newDonor.getEmail());
            throw new InternalErrorResponseStatusException();
        }

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @PostMapping("/update-donation")
    public ResponseEntity<Void> updateDonation(@RequestBody Donation donation) {

        if (donationService.saveDonation(donation) == null) {
            logService.logErrorMessage("DonationService::saveDonation", donation.getName());
            throw new InternalErrorResponseStatusException();
        }

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @GetMapping("/get-donors")
    public List<Donor> getDonors() {

        return donationService.getDonors();
    }
}
