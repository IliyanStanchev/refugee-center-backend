package bg.tuvarna.diploma_work.controllers;

import bg.tuvarna.diploma_work.enumerables.RequestStatus;
import bg.tuvarna.diploma_work.exceptions.CustomResponseStatusException;
import bg.tuvarna.diploma_work.exceptions.InternalErrorResponseStatusException;
import bg.tuvarna.diploma_work.models.LocationChangeRequest;
import bg.tuvarna.diploma_work.models.MedicalHelpRequest;
import bg.tuvarna.diploma_work.models.Refugee;
import bg.tuvarna.diploma_work.models.StockRequest;
import bg.tuvarna.diploma_work.services.*;
import com.sun.activation.registries.LogSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin(origins = {"http://localhost:3000"})
@RestController
public class RequestController {

    @Autowired
    private RequestService requestService;

    @Autowired
    private RefugeeService refugeeService;

    @Autowired
    private MailService mailService;

    @Autowired
    private TwilioService twilioService;

    @Autowired
    private LogService logService;

    @PostMapping("/request-stocks")
    public ResponseEntity<Void> requestStocks(@RequestBody StockRequest stockRequest ) {

        Refugee refugee = refugeeService.getRefugeeByUserId(stockRequest.getRefugee().getId());
        if( refugee == null )
        {
            logService.logErrorMessage("RefugeeService::getRefugeeByUserId",  stockRequest.getRefugee().getId() );
            throw new InternalErrorResponseStatusException();
        }

        stockRequest.setRefugee( refugee );
        stockRequest.setRequestStatus(RequestStatus.Pending);
        stockRequest.setDateCreated(LocalDateTime.now());
        stockRequest.setId(0L);

        if( requestService.save(stockRequest) == null){
            logService.logErrorMessage("RequestService::save",  stockRequest.toString() );
            throw new InternalErrorResponseStatusException();
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/request-location-change")
    public ResponseEntity<Void> requestLocationChange(@RequestBody LocationChangeRequest locationChangeRequest ) {

        Refugee refugee = refugeeService.getRefugeeByUserId(locationChangeRequest.getRefugee().getId());
        if( refugee == null )
        {
            logService.logErrorMessage("RefugeeService::getRefugeeByUserId",  locationChangeRequest.getRefugee().getId() );
            throw new InternalErrorResponseStatusException();
        }

        if( !requestService.checkForPendingLocationChangeRequest(refugee.getId()) ){
            throw new CustomResponseStatusException("You already have a pending location change request");
        }

        locationChangeRequest.setId(0L);
        locationChangeRequest.setRefugee( refugee );
        locationChangeRequest.setDateCreated( LocalDateTime.now() );
        locationChangeRequest.setRequestStatus(RequestStatus.Pending);

        if( requestService.save(locationChangeRequest) == null){
            logService.logErrorMessage("RequestService::save",  locationChangeRequest.toString() );
            throw new InternalErrorResponseStatusException();
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/request-medical-help")
    public ResponseEntity<Void> requestMedicalHelp(@RequestBody MedicalHelpRequest medicalHelpRequest ) {

        Refugee refugee = refugeeService.getRefugeeByUserId(medicalHelpRequest.getRefugee().getId());
        if( refugee == null )
        {
            logService.logErrorMessage("RefugeeService::getRefugeeByUserId",  medicalHelpRequest.getRefugee().getId() );
            throw new InternalErrorResponseStatusException();
        }

        medicalHelpRequest.setId(0L);
        medicalHelpRequest.setRefugee( refugee );
        medicalHelpRequest.setDateCreated( LocalDateTime.now() );

        if( requestService.save(medicalHelpRequest) == null){
            logService.logErrorMessage("RequestService::save",  medicalHelpRequest.toString() );
            throw new InternalErrorResponseStatusException();
        }

        if( !mailService.sendMedicalHelpRequestMail(refugee) ){
            logService.logErrorMessage("MailService::sendMedicalHelpRequestMail",  medicalHelpRequest.toString() );
            throw new InternalErrorResponseStatusException();
        }

        if( !twilioService.sendMessage( refugee.getFacility().getPhoneNumber(), "Medical help request from " + refugee.getName() ) ){
            logService.logErrorMessage("TwilioService::sendMessage",  medicalHelpRequest.toString() );
            throw new InternalErrorResponseStatusException();
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/get-stock-requests/{userId}")
    public List<StockRequest> getStockRequests(@PathVariable("userId") Long userId ) {

        return requestService.getStockRequests(userId);
    }

    @GetMapping("/get-location-change-requests/{userId}")
    public List<LocationChangeRequest> getLocationChangeRequests(@PathVariable("userId") Long userId ) {

        List<LocationChangeRequest> list = requestService.getLocationChangeRequests(userId);

        return list;
    }

    @GetMapping("/get-medical-help-requests/{userId}")
    public List<MedicalHelpRequest> getMedicalHelpRequests(@PathVariable("userId") Long userId ) {

        return requestService.getMedicalHelpRequests(userId);
    }

    @PostMapping("/decline-stock-requests")
    public ResponseEntity<Void> declineStockRequests(@RequestBody List<Long> requestIds ) {

        for( Long id : requestIds ){
            StockRequest stockRequest = requestService.getStockRequest(id);
            if( stockRequest == null ){
                logService.logErrorMessage("RequestService::getStockRequestById",  id.toString() );
                throw new InternalErrorResponseStatusException();
            }

            if( stockRequest.getRequestStatus() != RequestStatus.Pending ){
                continue;
            }

            stockRequest.setRequestStatus(RequestStatus.Declined);
            if( requestService.save(stockRequest) == null){
                logService.logErrorMessage("RequestService::save",  stockRequest.toString() );
                throw new InternalErrorResponseStatusException();
            }
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/decline-location-change-requests")
    public ResponseEntity<Void> declineLocationChangeRequests(@RequestBody List<Long> requestIds ) {

        for( Long id : requestIds ){
            LocationChangeRequest locationChangeRequest = requestService.getLocationChangeRequest(id);
            if( locationChangeRequest == null ){
                logService.logErrorMessage("RequestService::getLocationChangeRequestById",  id.toString() );
                throw new InternalErrorResponseStatusException();
            }

            if( locationChangeRequest.getRequestStatus() != RequestStatus.Pending ){
                continue;
            }

            locationChangeRequest.setRequestStatus(RequestStatus.Declined);
            if( requestService.save(locationChangeRequest) == null){
                logService.logErrorMessage("RequestService::save",  locationChangeRequest.toString() );
                throw new InternalErrorResponseStatusException();
            }
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
