package bg.tuvarna.diploma_work.services;

import bg.tuvarna.diploma_work.models.Facility;
import bg.tuvarna.diploma_work.repositories.FacilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FacilityService {

    @Autowired
    private FacilityRepository facilityRepository;

    public List<Facility> getAllShelters(){
        return facilityRepository.getAllShelters();
    }

    public List<Facility> getAll() {
        return facilityRepository.findAll();
    }

    public Facility getById(Long id) {

        Optional<Facility> optionalFacility = facilityRepository.findById(id);
        if( optionalFacility.isPresent() )
            return optionalFacility.get();

        return null;
    }

    public Facility saveFacility(Facility facility) {
        return facilityRepository.save(facility);
    }

    public List<Facility> getSheltersForTransfer(Long id) {
        return facilityRepository.getSheltersForTransfer(id);
    }
}
