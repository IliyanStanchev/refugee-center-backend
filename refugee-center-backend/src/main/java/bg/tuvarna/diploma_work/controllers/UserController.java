package bg.tuvarna.diploma_work.controllers;

import bg.tuvarna.diploma_work.enumerables.AccountStatusType;
import bg.tuvarna.diploma_work.enumerables.RoleType;
import bg.tuvarna.diploma_work.exceptions.CustomResponseStatusException;
import bg.tuvarna.diploma_work.exceptions.InternalErrorResponseStatusException;
import bg.tuvarna.diploma_work.models.*;
import bg.tuvarna.diploma_work.security.BCryptPasswordEncoderExtender;
import bg.tuvarna.diploma_work.services.*;
import bg.tuvarna.diploma_work.storages.AccountData;
import bg.tuvarna.diploma_work.utils.PasswordGeneratorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@CrossOrigin(origins = {"http://localhost:3000"})
@RestController
public class UserController {

    @Autowired
    private RefugeeService refugeeService;

    @Autowired
    private UserService userService;

    @Autowired
    private MailService mailService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private AccountStatusService accountStatusService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private FacilityService facilityService;

    @PostMapping("/authenticate-user")
    public ResponseEntity<User> authenticateUser(@RequestBody User user) {

        User currentUser = userService.authenticateUser(user);
        if( currentUser == null )
            throw new CustomResponseStatusException("Wrong username or password");

        if( currentUser.getAccountStatus().getAccountStatusType() == AccountStatusType.Blocked)
            throw new CustomResponseStatusException("Your account has been blocked by administrator");

        if( currentUser.getAccountStatus().getAccountStatusType() == AccountStatusType.Pending )
            throw new CustomResponseStatusException("Your account is not confirmed by administrator. Please be patient.");

        currentUser.getAccountStatus().setLastLogin(LocalDate.now());
        AccountStatus accountStatus = accountStatusService.updateAccountStatus(currentUser.getAccountStatus());
        currentUser.setAccountStatus(accountStatus);

        userService.updateUser(currentUser);

        return new ResponseEntity<User>( currentUser, HttpStatus.OK );
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> sendNewPassword(@RequestBody User user) {

        User currentUser = userService.getUserByEmail(user.getEmail());
        if( currentUser == null )
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        final String newPassword = PasswordGeneratorUtil.generatePassword();

        User modifiedUser = userService.changePassword(currentUser, newPassword);

        if( modifiedUser == null )
        {
            LogService.logErrorMessage("UserService::changePassword", modifiedUser.getEmail(), modifiedUser.getId());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        User databaseUser = userService.getUser(modifiedUser.getId());
        if( databaseUser == null )
        {
            LogService.logErrorMessage("CustomerService::getCustomerByUserID", String.valueOf(modifiedUser.getId()), modifiedUser.getId());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if( !mailService.sendResetPasswordEmail(databaseUser, newPassword) )
        {
            LogService.logErrorMessage("MailService::sendResetPasswordEmail", "", modifiedUser.getId());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/create-employee")
    public ResponseEntity<Void> createRefugee(@RequestBody User user) {

        user.setEmail(user.getEmail().toLowerCase(Locale.ROOT));

        validateUser(user);

        AccountStatus accountStatus = accountStatusService.createAccountStatus(AccountStatusType.Approved);
        if( accountStatus == null ) {
            LogService.logErrorMessage("AccountStatusService::createAccountStatus", user.getEmail() );
            throw new InternalErrorResponseStatusException();
        }

        user.setAccountStatus(accountStatus);

        final String newPassword = PasswordGeneratorUtil.generatePassword();
        user.setPassword(newPassword);

        User savedUser = userService.createOrUpdateUser( user, RoleType.Moderator);

        if( savedUser == null )
        {
            LogService.logErrorMessage("UserService::createOrUpdateUser", user.getEmail() );
            return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if( !groupService.addEmployeeToGroups(savedUser))
        {
            LogService.logErrorMessage("GroupService::addEmployeeToGroups", user.getEmail() );
            return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if( !mailService.sendNewUserEmail(savedUser, newPassword) )
        {
            LogService.logErrorMessage("MailService::sendResetPasswordEmail", "", user.getId());
            return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @PostMapping("/create-refugee")
    @Transactional
    public ResponseEntity<Void> createRefugee(@RequestBody Refugee refugee) {

        refugee.getUser().setEmail(refugee.getUser().getEmail().toLowerCase(Locale.ROOT));

        validateUser(refugee.getUser());

        final String newPassword = PasswordGeneratorUtil.generatePassword();
        refugee.getUser().setPassword(newPassword);

        AccountStatus accountStatus = accountStatusService.createAccountStatus(AccountStatusType.Approved);

        if( accountStatus == null ) {
            LogService.logErrorMessage("AccountStatusService::createAccountStatus", refugee.getUser().getEmail() );
            throw new InternalErrorResponseStatusException();
        }

        refugee.getUser().setAccountStatus(accountStatus);

        User savedUser = userService.createOrUpdateUser( refugee.getUser(), RoleType.Refugee);

        if( savedUser == null ) {
            LogService.logErrorMessage("UserService::createOrUpdateUser", refugee.getUser().getEmail() );
            throw new InternalErrorResponseStatusException();
        }

        refugee.setUser(savedUser);

       Address savedAddress = addressService.saveAddress(refugee.getAddress());
        if( savedAddress == null ) {
            LogService.logErrorMessage("AddressService::createAddress", refugee.getAddress().toString());
            throw new InternalErrorResponseStatusException();
        }

        refugee.setAddress(savedAddress);

        Refugee savedRefugee = refugeeService.saveRefugee( refugee );

        if( savedRefugee == null ){
            LogService.logErrorMessage("RefugeeService::createRefugee",  refugee.getUser().getEmail() );
            throw new InternalErrorResponseStatusException();
        }

        if( !groupService.addRefugeeToGroups(refugee.getUser()))
        {
            LogService.logErrorMessage("GroupService::addRefugeeToGroups",  refugee.getUser().getEmail() );
            throw new InternalErrorResponseStatusException();
        }

        Facility facility = facilityService.getById(refugee.getFacility().getId());
        if( facility == null )
        {
            LogService.logErrorMessage("FacilityService::getById",  refugee.getFacility().getId() );
            throw new InternalErrorResponseStatusException();
        }

        facility.setCurrentCapacity(facility.getCurrentCapacity() + 1);
        if( facilityService.saveFacility(facility) == null )
        {
            LogService.logErrorMessage("FacilityService::saveFacility",  refugee.getFacility().getId() );
            throw new InternalErrorResponseStatusException();
        }

        if( !mailService.sendNewUserEmail(savedUser, newPassword) )
        {
            LogService.logErrorMessage("MailService::sendResetPasswordEmail", "", refugee.getId());
            throw new InternalErrorResponseStatusException();
        }

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @PostMapping("/validate-user-creation")
    public ResponseEntity<Void> validateUserCreation(@RequestBody User user) {

        user.setEmail(user.getEmail().toLowerCase(Locale.ROOT));

        validateUser(user);

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    private void validateUser( User user ){

        User currentUser = userService.getUserByEmail(user.getEmail());
        if( currentUser != null )
            throw new CustomResponseStatusException("User with this email already exists");

        currentUser = userService.getUserByIdentifier(user.getIdentifier());
        if (currentUser != null)
            throw new CustomResponseStatusException("User with this identifier already exists");
    }

    @GetMapping("/get-responsible-users")
    public List<User> getResponsibleUsers() {

        return userService.getResponsibleUsers();
    }

    @GetMapping("/get-user/{id}")
    public User getUser(@PathVariable("id") Long id) {
        return userService.getUser(id);
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(@RequestBody AccountData accountData) {

        User user = userService.getUser(accountData.getId());
        if (user == null) {
            LogService.logErrorMessage("UserService::getUser", accountData.getId());
            throw new InternalErrorResponseStatusException();
        }

        BCryptPasswordEncoderExtender encoder = new BCryptPasswordEncoderExtender();
        if (!encoder.matches(accountData.getOldPassword(), user.getPassword()))
            throw new CustomResponseStatusException("Old password is incorrect");

        user.getAccountStatus().setLastPasswordChangeDate(LocalDate.now());

        AccountStatus accountStatus = accountStatusService.updateAccountStatus(user.getAccountStatus());
        if (accountStatus == null) {
            LogService.logErrorMessage("AccountStatusService::updateAccountStatus", accountData.getId());
            throw new InternalErrorResponseStatusException();
        }

        user.setAccountStatus(accountStatus);
        user.setPassword(encoder.encode(accountData.getNewPassword()));
        if (userService.updateUser(user) == null)
        {
            LogService.logErrorMessage("UserService::updateUser", accountData.getId());
            throw new InternalErrorResponseStatusException();
        }

        return new ResponseEntity<Void>(HttpStatus.OK);
    }
    
}
