package bg.tuvarna.diploma_work.controllers;

import bg.tuvarna.diploma_work.enumerables.DonationType;
import bg.tuvarna.diploma_work.models.Donation;
import bg.tuvarna.diploma_work.services.DonationService;
import bg.tuvarna.diploma_work.services.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = {"http://localhost:3000"})
@RestController
public class DonationController {

    @Autowired
    DonationService donationService;

    @PostMapping("/donate-money")
    public ResponseEntity<Void> donateMoney(@RequestBody Donation donation) {

        donation.setDonationType(DonationType.MONEY);
        Donation savedDonation = donationService.saveDonation( donation );

        if( savedDonation == null )
        {
            LogService.logErrorMessage("DonationService::saveDonation", String.valueOf(donation.getQuantity()) + donation.getUnit());
            return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}
