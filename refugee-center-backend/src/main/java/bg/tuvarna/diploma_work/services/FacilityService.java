package bg.tuvarna.diploma_work.services;

import bg.tuvarna.diploma_work.models.Facility;
import bg.tuvarna.diploma_work.repositories.FacilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FacilityService {

    @Autowired
    private FacilityRepository facilityRepository;

    public List<Facility> getAllShelters(){
        return facilityRepository.getAllShelters();
    }
}
