package bg.tuvarna.diploma_work.controllers;

import bg.tuvarna.diploma_work.exceptions.InternalErrorResponseStatusException;
import bg.tuvarna.diploma_work.models.Donation;
import bg.tuvarna.diploma_work.models.DonationAbsorption;
import bg.tuvarna.diploma_work.models.Facility;
import bg.tuvarna.diploma_work.services.DonationAbsorptionService;
import bg.tuvarna.diploma_work.services.FacilityService;
import bg.tuvarna.diploma_work.services.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = {"http://localhost:3000"})
@RestController
public class DonationAbsorptionController {

    @Autowired
    private DonationAbsorptionService donationAbsorptionService;

    @Autowired
    private FacilityService facilityService;

    @Autowired
    private LogService logService;

    @GetMapping("/get-donation-absorptions/{id}")
    public List<DonationAbsorption> getDonationAbsorptions(@PathVariable Long id) {
        return donationAbsorptionService.getDonationAbsorptions(id);
    }

    @GetMapping("/get-new-shelter-absorptions/{id}")
    public List<Donation> getNewShelterAbsorptions(@PathVariable Long id) {
        return donationAbsorptionService.getNewShelterAbsorptions(id);
    }

    @PostMapping("/save-donation-absorption")
    public ResponseEntity<Void> saveDonationAbsorption(@RequestBody DonationAbsorption donationAbsorption) {

        Facility facility = facilityService.getById(donationAbsorption.getFacility().getId());
        if (facility == null) {
            logService.logErrorMessage("FacilityService::getById", donationAbsorption.getFacility().getId());
            throw new InternalErrorResponseStatusException();
        }

        if (donationAbsorptionService.saveDonationAbsorption(donationAbsorption) == null) {
            logService.logErrorMessage("DonationAbsorptionService::saveDonationAbsorption", donationAbsorption.getId());
            throw new InternalErrorResponseStatusException();
        }

        return new ResponseEntity<>(HttpStatus.OK);

    }
}
