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
import java.util.Optional;

@Service
public class RequestService {

    @Autowired
    private MedicalHelpRequestRepository medicalHelpRequestRepository;

    @Autowired
    private LocationChangeRequestRepository locationChangeRequestRepository;

    @Autowired
    private StockRequestRepository stockRequestRepository;

    public MedicalHelpRequest save(MedicalHelpRequest medicalHelpRequest) {

        return medicalHelpRequestRepository.save(medicalHelpRequest);
    }


    public LocationChangeRequest save(LocationChangeRequest locationChangeRequest) {

        return locationChangeRequestRepository.save(locationChangeRequest);
    }


    public StockRequest save(StockRequest stockRequest) {

        return stockRequestRepository.save(stockRequest);
    }

    public boolean checkForPendingLocationChangeRequest(Long refugeeId) {

        return locationChangeRequestRepository.checkForPendingLocationChangeRequest(refugeeId) == null;
    }

    public List<MedicalHelpRequest> getMedicalHelpRequests(Long id) {

        return medicalHelpRequestRepository.getMedicalHelpRequests(id);
    }

    public List<StockRequest> getStockRequests(Long userId) {

        return stockRequestRepository.getStockRequests(userId);
    }

    public List<LocationChangeRequest> getLocationChangeRequests(Long userId) {

        return locationChangeRequestRepository.getLocationChangeRequests(userId);
    }

    public StockRequest getStockRequest(Long id) {

        Optional<StockRequest> stockRequest = stockRequestRepository.findById(id);
        if (stockRequest.isPresent())
            return stockRequest.get();

        return null;
    }

    public LocationChangeRequest getLocationChangeRequest(Long id) {

        Optional<LocationChangeRequest> locationChangeRequest = locationChangeRequestRepository.findById(id);
        if (locationChangeRequest.isPresent())
            return locationChangeRequest.get();

        return null;
    }

    public List<StockRequest> getPendingStockRequests(Long userId) {

        return stockRequestRepository.getPendingStockRequests(userId);
    }

    public List<LocationChangeRequest> getPendingLocationChangeRequests(Long userId) {

        return locationChangeRequestRepository.getPendingLocationChangeRequests(userId);
    }

    public List<MedicalHelpRequest> getRefugeesMedicalHelpRequests(Long userId) {

        return medicalHelpRequestRepository.getRefugeesMedicalHelpRequests(userId);
    }
}
