package bg.tuvarna.diploma_work.controllers;

import bg.tuvarna.diploma_work.models.Facility;
import bg.tuvarna.diploma_work.services.FacilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = {"http://localhost:3000"})
@RestController
public class FacilityController {

    @Autowired
    FacilityService facilityService;

    @GetMapping("/get-all-shelters")
    public List<Facility> getAllShelters() {

        return facilityService.getAllShelters();
    }
}