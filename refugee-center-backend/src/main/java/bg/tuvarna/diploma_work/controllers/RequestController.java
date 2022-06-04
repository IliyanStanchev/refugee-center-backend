package bg.tuvarna.diploma_work.controllers;

import bg.tuvarna.diploma_work.enumerables.RequestStatus;
import bg.tuvarna.diploma_work.exceptions.CustomResponseStatusException;
import bg.tuvarna.diploma_work.exceptions.InternalErrorResponseStatusException;
import bg.tuvarna.diploma_work.models.LocationChangeRequest;
import bg.tuvarna.diploma_work.models.MedicalHelpRequest;
import bg.tuvarna.diploma_work.models.Refugee;
import bg.tuvarna.diploma_work.models.StockRequest;
import bg.tuvarna.diploma_work.services.LogService;
import bg.tuvarna.diploma_work.services.MailService;
import bg.tuvarna.diploma_work.services.RefugeeService;
import bg.tuvarna.diploma_work.services.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = {"http://localhost:3000"})
@RestController
public class RequestController {

    @Autowired
    private RequestService requestService;

    @Autowired
    private RefugeeService refugeeService;

    @Autowired
    private MailService mailService;

    @PostMapping("/request-stocks")
    public ResponseEntity<Void> requestStocks(@RequestBody StockRequest stockRequest ) {

        Refugee refugee = refugeeService.getRefugeeByUserId(stockRequest.getRefugee().getId());
        if( refugee == null )
        {
            LogService.logErrorMessage("RefugeeService::getRefugeeByUserId",  stockRequest.getRefugee().getId() );
            throw new InternalErrorResponseStatusException();
        }

        stockRequest.setRefugee( refugee );
        stockRequest.setRequestStatus(RequestStatus.Pending);
        stockRequest.setId(0L);

        if( requestService.save(stockRequest) == null){
            LogService.logErrorMessage("RequestService::save",  stockRequest.toString() );
            throw new InternalErrorResponseStatusException();
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/request-location-change")
    public ResponseEntity<Void> requestLocationChange(@RequestBody LocationChangeRequest locationChangeRequest ) {

        Refugee refugee = refugeeService.getRefugeeByUserId(locationChangeRequest.getRefugee().getId());
        if( refugee == null )
        {
            LogService.logErrorMessage("RefugeeService::getRefugeeByUserId",  locationChangeRequest.getRefugee().getId() );
            throw new InternalErrorResponseStatusException();
        }

        if( requestService.checkForPendingLocationChangeRequest(refugee.getId()) ){
            throw new CustomResponseStatusException("You already have a pending location change request");
        }

        locationChangeRequest.setId(0L);
        locationChangeRequest.setRefugee( refugee );
        locationChangeRequest.setRequestStatus(RequestStatus.Pending);

        if( requestService.save(locationChangeRequest) == null){
            LogService.logErrorMessage("RequestService::save",  locationChangeRequest.toString() );
            throw new InternalErrorResponseStatusException();
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/request-medical-help")
    public ResponseEntity<Void> requestMedicalHelp(@RequestBody MedicalHelpRequest medicalHelpRequest ) {

        Refugee refugee = refugeeService.getRefugeeByUserId(medicalHelpRequest.getRefugee().getId());
        if( refugee == null )
        {
            LogService.logErrorMessage("RefugeeService::getRefugeeByUserId",  medicalHelpRequest.getRefugee().getId() );
            throw new InternalErrorResponseStatusException();
        }

        medicalHelpRequest.setId(0L);
        medicalHelpRequest.setRefugee( refugee );

        if( requestService.save(medicalHelpRequest) == null){
            LogService.logErrorMessage("RequestService::save",  medicalHelpRequest.toString() );
            throw new InternalErrorResponseStatusException();
        }

        if( !mailService.sendMedicalHelpRequestMail(refugee) ){
            LogService.logErrorMessage("MailService::sendMedicalHelpRequestMail",  medicalHelpRequest.toString() );
            throw new InternalErrorResponseStatusException();
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
