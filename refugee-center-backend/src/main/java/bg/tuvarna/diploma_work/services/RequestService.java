package bg.tuvarna.diploma_work.services;

import bg.tuvarna.diploma_work.models.LocationChangeRequest;
import bg.tuvarna.diploma_work.models.MedicalHelpRequest;
import bg.tuvarna.diploma_work.models.StockRequest;
import bg.tuvarna.diploma_work.repositories.LocationChangeRequestRepository;
import bg.tuvarna.diploma_work.repositories.MedicalHelpRequestRepository;
import bg.tuvarna.diploma_work.repositories.StockRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RequestService {

    @Autowired
    private MedicalHelpRequestRepository medicalHelpRequestRepository;

    @Autowired
    private LocationChangeRequestRepository locationChangeRequestRepository;

    @Autowired
    private StockRequestRepository stockRequestRepository;

    public MedicalHelpRequest save( MedicalHelpRequest medicalHelpRequest ){

        return medicalHelpRequestRepository.save(medicalHelpRequest);
    }


    public LocationChangeRequest save( LocationChangeRequest locationChangeRequest ){

        return locationChangeRequestRepository.save(locationChangeRequest);
    }


    public StockRequest save( StockRequest stockRequest ){

        return stockRequestRepository.save(stockRequest);
    }

    public boolean checkForPendingLocationChangeRequest(Long refugeeId) {

        return locationChangeRequestRepository.checkForPendingLocationChangeRequest(refugeeId) == null;
    }
}
