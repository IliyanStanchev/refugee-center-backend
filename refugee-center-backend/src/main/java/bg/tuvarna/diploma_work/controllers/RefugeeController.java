package bg.tuvarna.diploma_work.controllers;

import bg.tuvarna.diploma_work.enumerables.AccountStatusType;
import bg.tuvarna.diploma_work.enumerables.RoleType;
import bg.tuvarna.diploma_work.exceptions.InternalErrorResponseStatusException;
import bg.tuvarna.diploma_work.models.Refugee;
import bg.tuvarna.diploma_work.models.User;
import bg.tuvarna.diploma_work.services.*;
import bg.tuvarna.diploma_work.utils.PasswordGeneratorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = {"http://localhost:3000"})
@RestController
public class RefugeeController {

    @Autowired
    private RefugeeService refugeeService;

    @Autowired
    private MailService mailService;

    @Autowired
    private AccountStatusService accountStatusService;

    @Autowired
    private UserService userService;

    @GetMapping("/get-pending-registrations")
    public List<Refugee> getPendingRegistrations() {

        return refugeeService.getPendingRegistrations();
    }

    @PostMapping("/delete-pending-registrations")
    @Transactional
    public ResponseEntity<Void> deletePendingRegistrations(@RequestBody List<Long> registrationsList) {

        for( Long refugeeId : registrationsList ){

            Refugee refugee = refugeeService.getRefugeeByID(refugeeId);
            if( refugee == null )
                continue;

            processDecline(refugee);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/approve-pending-registrations")
    @Transactional
    public ResponseEntity<Void> approvePendingRegistrations(@RequestBody List<Long> registrationsList) {

        for( Long refugeeId : registrationsList ){

            Refugee refugee = refugeeService.getRefugeeByID(refugeeId);
            if( refugee == null )
                continue;

            processApprove(refugee);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/delete-single-registration")
    public ResponseEntity<Void> deletePendingRegistration(@RequestBody Refugee refugee) {

        return processDecline( refugee );
    }

    @PostMapping("/approve-single-registration")
    @Transactional
    public ResponseEntity<Void> approvePendingRegistration(@RequestBody Refugee refugee) {

       return processApprove( refugee );
    }

    private ResponseEntity<Void> processApprove( Refugee refugee ){

        refugee.getUser().getAccountStatus().setAccountStatusType(AccountStatusType.APPROVED);

        if( accountStatusService.updateAccountStatus(refugee.getUser().getAccountStatus() ) == null ){
            LogService.logErrorMessage("RefugeeService::declinePendingRegistration", refugee.getUser().getEmail() );
            throw new InternalErrorResponseStatusException();
        }

        final String newPassword = PasswordGeneratorUtil.generatePassword();
        refugee.getUser().setPassword(newPassword);

        User savedUser = userService.createOrUpdateUser( refugee.getUser(), RoleType.REFUGEE);

        if( savedUser == null ){
            LogService.logErrorMessage("UserService::createOrUpdateUser", refugee.getUser().getEmail() );
            throw new InternalErrorResponseStatusException();
        }

        if( !mailService.sendNewUserEmail(refugee.getUser(), newPassword))
        {
            LogService.logErrorMessage("MailService::sendDeclinedRegistrationEmail", "", refugee.getId());
            throw new InternalErrorResponseStatusException();
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private ResponseEntity<Void> processDecline( Refugee refugee ){

        refugee.getUser().getAccountStatus().setAccountStatusType(AccountStatusType.DECLINED);

        if( accountStatusService.updateAccountStatus(refugee.getUser().getAccountStatus() ) == null ){
            LogService.logErrorMessage("RefugeeService::declinePendingRegistration", refugee.getUser().getEmail() );
            throw new InternalErrorResponseStatusException();
        }

        if( !mailService.sendDeclinedRegistrationEmail(refugee.getUser()))
        {
            LogService.logErrorMessage("MailService::sendDeclinedRegistrationEmail", "", refugee.getId());
            throw new InternalErrorResponseStatusException();
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
