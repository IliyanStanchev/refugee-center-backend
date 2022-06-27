package bg.tuvarna.diploma_work.controllers;

import bg.tuvarna.diploma_work.enumerables.AccountStatusType;
import bg.tuvarna.diploma_work.enumerables.RoleType;
import bg.tuvarna.diploma_work.exceptions.CustomResponseStatusException;
import bg.tuvarna.diploma_work.exceptions.InternalErrorResponseStatusException;
import bg.tuvarna.diploma_work.helpers.CharSequenceGenerator;
import bg.tuvarna.diploma_work.models.AccountStatus;
import bg.tuvarna.diploma_work.models.Facility;
import bg.tuvarna.diploma_work.models.Refugee;
import bg.tuvarna.diploma_work.models.User;
import bg.tuvarna.diploma_work.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

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

    @Autowired
    private FacilityService facilityService;

    @Autowired
    private LogService logService;

    @Autowired
    private UserSessionService userSessionService;

    @GetMapping("/get-pending-registrations")
    public List<Refugee> getPendingRegistrations() {

        return refugeeService.getPendingRegistrations();
    }

    @PostMapping("/delete-pending-registrations")
    @Transactional
    public ResponseEntity<Void> deletePendingRegistrations(@RequestBody List<Long> registrationsList) {

        for (Long refugeeId : registrationsList) {

            Refugee refugee = refugeeService.getRefugeeByID(refugeeId);
            if (refugee == null)
                continue;

            processDecline(refugee);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/approve-pending-registrations")
    @Transactional
    public ResponseEntity<Void> approvePendingRegistrations(@RequestBody List<Long> registrationsList) {

        for (Long refugeeId : registrationsList) {

            Refugee refugee = refugeeService.getRefugeeByID(refugeeId);
            if (refugee == null)
                continue;

            processApprove(refugee);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/delete-single-registration")
    public ResponseEntity<Void> deletePendingRegistration(@RequestBody Refugee refugee) {

        return processDecline(refugee);
    }

    @PostMapping("/approve-single-registration")
    @Transactional
    public ResponseEntity<Void> approvePendingRegistration(@RequestBody Refugee refugee) {

        return processApprove(refugee);
    }

    private ResponseEntity<Void> processApprove(Refugee refugee) {

        refugee.getUser().getAccountStatus().setAccountStatusType(AccountStatusType.Approved);

        if (accountStatusService.updateAccountStatus(refugee.getUser().getAccountStatus()) == null) {
            logService.logErrorMessage("RefugeeService::declinePendingRegistration", refugee.getUser().getEmail());
            throw new InternalErrorResponseStatusException();
        }

        final String newPassword = CharSequenceGenerator.generatePassword();
        refugee.getUser().setPassword(newPassword);

        User savedUser = userService.createOrUpdateUser(refugee.getUser(), RoleType.Refugee);

        if (savedUser == null) {
            logService.logErrorMessage("UserService::createOrUpdateUser", refugee.getUser().getEmail());
            throw new InternalErrorResponseStatusException();
        }

        if (!mailService.sendNewUserEmail(refugee.getUser(), newPassword)) {
            logService.logErrorMessage("MailService::sendDeclinedRegistrationEmail", "");
            throw new InternalErrorResponseStatusException();
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private ResponseEntity<Void> processDecline(Refugee refugee) {

        refugee.getUser().getAccountStatus().setAccountStatusType(AccountStatusType.Declined);

        if (accountStatusService.updateAccountStatus(refugee.getUser().getAccountStatus()) == null) {
            logService.logErrorMessage("RefugeeService::declinePendingRegistration", refugee.getUser().getEmail());
            throw new InternalErrorResponseStatusException();
        }

        if (!mailService.sendDeclinedRegistrationEmail(refugee.getUser())) {
            logService.logErrorMessage("MailService::sendDeclinedRegistrationEmail", "");
            throw new InternalErrorResponseStatusException();
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/get-refugees-in-shelter/{id}")
    public List<Refugee> getRefugeesInShelter(@PathVariable Long id) {

        return refugeeService.getRefugeesInShelter(id);
    }

    @PutMapping("/remove-refugee-from-shelter/{refugeeId}")
    @Transactional
    public ResponseEntity<Void> removeRefugeeFromShelter(@PathVariable Long refugeeId) {

        Refugee refugee = refugeeService.getRefugeeByID(refugeeId);

        if (refugee == null) {
            logService.logErrorMessage("RefugeeService::getRefugeeByID", refugeeId);
            throw new InternalErrorResponseStatusException();
        }

        Facility facility = facilityService.getById(refugee.getFacility().getId());
        if (facility == null) {
            logService.logErrorMessage("FacilityService::getById", refugee.getFacility().getId());
            throw new InternalErrorResponseStatusException();
        }

        facility.setCurrentCapacity(facility.getCurrentCapacity() - 1);

        if (facilityService.saveFacility(facility) == null) {
            logService.logErrorMessage("FacilityService::saveFacility", refugee.getFacility().getId());
            throw new InternalErrorResponseStatusException();
        }

        refugeeService.removeRefugeeFromShelter(refugeeId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/get-users-without-shelter")
    public List<Refugee> getUsersWithoutShelter() {
        return refugeeService.getUsersWithoutShelter();
    }

    @PutMapping("/add-refugee-to-shelter/{userId}/{shelterId}")
    @Transactional
    public ResponseEntity<Void> addRefugeeToShelter(@PathVariable Long userId, @PathVariable Long shelterId) {

        Refugee refugee = refugeeService.getRefugeeByUserId(userId);
        if (refugee == null) {
            logService.logErrorMessage("RefugeeService::getRefugeeByUserId", userId);
            throw new InternalErrorResponseStatusException();
        }

        Facility facility = facilityService.getById(shelterId);
        if (facility == null) {
            logService.logErrorMessage("FacilityService::getById", refugee.getFacility().getId());
            throw new InternalErrorResponseStatusException();
        }

        facility.setCurrentCapacity(facility.getCurrentCapacity() + 1);

        refugeeService.addRefugeeToShelter(shelterId, refugee.getId());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/get-refugee-by-user-id/{userId}")
    public ResponseEntity<Refugee> getRefugeeByUserId(@PathVariable Long userId) {

        Refugee refugee = refugeeService.getRefugeeByUserId(userId);
        if (refugee == null) {
            logService.logErrorMessage("RefugeeService::getRefugeeByUserId", userId);
            throw new InternalErrorResponseStatusException();
        }

        return new ResponseEntity<>(refugee, HttpStatus.OK);
    }

    @PatchMapping("/update-refugee")
    @Transactional
    public ResponseEntity<Refugee> updateRefugee(@RequestBody Refugee refugee) {

        User user = refugee.getUser();
        user.setEmail(user.getEmail().toLowerCase(Locale.ROOT));

        if (userService.checkEmailExists(user) != null) {
            throw new CustomResponseStatusException("User with this email already exists");
        }

        if (userService.checkIdentifierExists(user) != null) {
            throw new CustomResponseStatusException("User with this identifier already exists");
        }

        user = userService.updateUser(user);
        if (user == null) {
            logService.logErrorMessage("UserService::updateUser", user.getId());
            throw new InternalErrorResponseStatusException();
        }

        if (refugeeService.checkPhoneExists(refugee.getPhoneNumber(), refugee.getId()) != null)
            throw new CustomResponseStatusException("User with this phone number already exists");

        Refugee updatedRefugee = refugeeService.saveRefugee(refugee);
        if (updatedRefugee == null) {
            logService.logErrorMessage("RefugeeService::saveRefugee", refugee.getId());
            throw new InternalErrorResponseStatusException();
        }

        updatedRefugee.setUser(user);
        return new ResponseEntity<>(updatedRefugee, HttpStatus.OK);
    }

    @PutMapping("/change-refugee-status/{id}")
    @Transactional
    public ResponseEntity<Refugee> changeRefugeeStatus(@PathVariable Long id) {

        Refugee refugee = refugeeService.getRefugeeByUserId(id);
        if (refugee == null) {
            logService.logErrorMessage("RefugeeService::getRefugeeByID", id);
            throw new InternalErrorResponseStatusException();
        }

        AccountStatus accountStatus = refugee.getUser().getAccountStatus();
        accountStatus.setAccountStatusType(accountStatus.getAccountStatusType() == AccountStatusType.Blocked ?
                AccountStatusType.Verified
                : AccountStatusType.Blocked);

        userSessionService.closeSessions(id);

        accountStatus = accountStatusService.updateAccountStatus(accountStatus);
        if (accountStatus == null) {
            logService.logErrorMessage("AccountStatusService::updateAccountStatus", id);
            throw new InternalErrorResponseStatusException();
        }

        refugee.getUser().setAccountStatus(accountStatus);
        return new ResponseEntity<Refugee>(refugee, HttpStatus.OK);
    }


}
