package bg.tuvarna.diploma_work.controllers;

import bg.tuvarna.diploma_work.exceptions.InternalErrorResponseStatusException;
import bg.tuvarna.diploma_work.models.Address;
import bg.tuvarna.diploma_work.models.Facility;
import bg.tuvarna.diploma_work.models.Refugee;
import bg.tuvarna.diploma_work.services.AddressService;
import bg.tuvarna.diploma_work.services.FacilityService;
import bg.tuvarna.diploma_work.services.LogService;
import bg.tuvarna.diploma_work.services.RefugeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = {"http://localhost:3000"})
@RestController
public class FacilityController {

    @Autowired
    private FacilityService facilityService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private RefugeeService refugeeService;

    @Autowired
    private LogService logService;

    @GetMapping("/get-all-shelters")
    public List<Facility> getAllShelters() {

        return facilityService.getAllShelters();
    }

    @GetMapping("/get-all-facilities")
    public List<Facility> getAllFacilities() {

        return facilityService.getAll();
    }

    @PostMapping("/save-facility")
    public ResponseEntity<Void> saveFacility(@RequestBody Facility facility){

        Address address = addressService.saveAddress(facility.getAddress());
        facility.setAddress(address);

        if( facilityService.saveFacility(facility) == null )
        {
            logService.logErrorMessage("FacilityService::saveFacility",  facility.getAddress().toString() );
            throw new InternalErrorResponseStatusException();
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/get-shelters-for-transfer/{id}")
    public List<Facility> getSheltersForTransfer(@PathVariable("id") Long id) {

        Refugee refugee = refugeeService.getRefugeeByUserId(id);
        if( refugee == null )
        {
            logService.logErrorMessage("RefugeeService::getRefugeeByUserId",  id );
            throw new InternalErrorResponseStatusException();
        }

        return facilityService.getSheltersForTransfer(refugee.getFacility().getId());
    }
}