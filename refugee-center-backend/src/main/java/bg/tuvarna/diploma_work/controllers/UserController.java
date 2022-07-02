package bg.tuvarna.diploma_work.controllers;

import bg.tuvarna.diploma_work.enumerables.AccountStatusType;
import bg.tuvarna.diploma_work.enumerables.RoleType;
import bg.tuvarna.diploma_work.enumerables.VerificationCodeType;
import bg.tuvarna.diploma_work.exceptions.CustomResponseStatusException;
import bg.tuvarna.diploma_work.exceptions.InternalErrorResponseStatusException;
import bg.tuvarna.diploma_work.exceptions.UnauthorizedUserResponseStatusException;
import bg.tuvarna.diploma_work.helpers.CharSequenceGenerator;
import bg.tuvarna.diploma_work.models.*;
import bg.tuvarna.diploma_work.security.BCryptPasswordEncoderExtender;
import bg.tuvarna.diploma_work.services.*;
import bg.tuvarna.diploma_work.storages.AccountData;
import bg.tuvarna.diploma_work.storages.RefugeeRegistrationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
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
    private VerificationCodeService verificationCodeService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private AccountStatusService accountStatusService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private FacilityService facilityService;

    @Autowired
    private LogService logService;

    @Autowired
    private UserSessionService userSessionService;

    @PostMapping("/authenticate-user")
    @Transactional
    public ResponseEntity<UserSession> authenticateUser(@RequestBody User user) {

        user.setEmail(user.getEmail().toLowerCase(Locale.ROOT));

        User currentUser = userService.authenticateUser(user);
        if (currentUser == null)
            throw new CustomResponseStatusException("Wrong username or password");

        if (currentUser.getAccountStatus().getAccountStatusType() == AccountStatusType.Blocked)
            throw new CustomResponseStatusException("Your account has been blocked by administrator");

        if (currentUser.getAccountStatus().getAccountStatusType() == AccountStatusType.Pending)
            throw new CustomResponseStatusException("Your account is not confirmed by administrator. Please be patient.");

        currentUser.getAccountStatus().setLastLogin(LocalDate.now());
        AccountStatus accountStatus = accountStatusService.updateAccountStatus(currentUser.getAccountStatus());
        currentUser.setAccountStatus(accountStatus);

        userService.updateUser(currentUser);

        final String authenticationToken = CharSequenceGenerator.generateAuthenticationCode();
        UserSession userSession = new UserSession();
        userSession.setUser(currentUser);
        userSession.setCreationDate(LocalDateTime.now());
        userSession.setExpirationDate(LocalDateTime.now().plusHours(1));
        userSession.setActiveSession(true);

        BCryptPasswordEncoderExtender bCryptPasswordEncoderExtender = new BCryptPasswordEncoderExtender();
        userSession.setAuthorizationToken(bCryptPasswordEncoderExtender.encode(authenticationToken));

        userSessionService.closeSessions(currentUser.getId());

        userSession = userSessionService.saveUserSession(userSession);

        if (userSession == null) {
            logService.logErrorMessage("UserSessionService::changePassword", currentUser.getId());
            throw new InternalErrorResponseStatusException();
        }

        UserSession currentUserSession = new UserSession();
        currentUserSession.setUser(currentUser);
        currentUserSession.setAuthorizationToken(Base64.getEncoder().encodeToString(authenticationToken.getBytes()));

        return new ResponseEntity<UserSession>(currentUserSession, HttpStatus.OK);
    }

    @PostMapping("/forgot-password")
    @Transactional
    public ResponseEntity<Void> sendNewPassword(@RequestBody User user) {

        User currentUser = userService.getUserByEmail(user.getEmail());
        if (currentUser == null)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        final String resetPasswordToken = CharSequenceGenerator.generatePasswordResetToken();

        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setVerificationCodeType(VerificationCodeType.PasswordReset);
        verificationCode.setUser(currentUser);
        verificationCode.setCode(resetPasswordToken);

        if (!mailService.sendResetPasswordEmail(currentUser, resetPasswordToken)) {
            logService.logErrorMessage("MailService::sendResetPasswordEmail", "");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        verificationCodeService.deleteVerificationCodes(currentUser.getId());

        if( verificationCodeService.saveVerificationCode(verificationCode) == null ) {
            logService.logErrorMessage("VerificationCodeService::saveVerificationCode", "");
            throw new InternalErrorResponseStatusException();
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/create-employee")
    @Transactional
    public ResponseEntity<Void> createEmployee(@RequestBody User user) {

        user.setEmail(user.getEmail().toLowerCase(Locale.ROOT));

        validateUser(user);

        AccountStatus accountStatus = accountStatusService.saveAccountStatus(AccountStatusType.Verified);
        if (accountStatus == null) {
            logService.logErrorMessage("AccountStatusService::createAccountStatus", user.getEmail());
            throw new InternalErrorResponseStatusException();
        }

        user.setAccountStatus(accountStatus);

        final String passwordToken = CharSequenceGenerator.generatePasswordResetToken();
        user.setPassword(passwordToken);

        User savedUser = userService.createOrUpdateUser(user, RoleType.Moderator);

        if (savedUser == null) {
            logService.logErrorMessage("UserService::createOrUpdateUser", user.getEmail());
            return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setVerificationCodeType(VerificationCodeType.NewAccount);
        verificationCode.setUser(savedUser);
        verificationCode.setCode(passwordToken);

        if( verificationCodeService.saveVerificationCode(verificationCode) == null ) {
            logService.logErrorMessage("VerificationCodeService::saveVerificationCode", "");
            throw new InternalErrorResponseStatusException();
        }

        if (!groupService.addEmployeeToGroups(savedUser)) {
            logService.logErrorMessage("GroupService::addEmployeeToGroups", user.getEmail());
            return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (!mailService.sendNewUserEmail(savedUser, passwordToken)) {
            logService.logErrorMessage("MailService::sendResetPasswordEmail", "");
            return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @PostMapping("/create-refugee")
    @Transactional
    public ResponseEntity<RoleType> createRefugee(@RequestBody RefugeeRegistrationData refugeeRegistrationData) {

        Refugee refugee = refugeeRegistrationData.getRefugee();

        User employee = userService.getUser(refugeeRegistrationData.getEmployeeId());
        if (employee == null) {
            logService.logErrorMessage("UserService::getUser", String.valueOf(refugeeRegistrationData.getEmployeeId()));
            throw new InternalErrorResponseStatusException();
        }

        final RoleType roleType = employee.getRole().getRoleType();

        refugee.getUser().setEmail(refugee.getUser().getEmail().toLowerCase(Locale.ROOT));

        validateUser(refugee.getUser());

        Refugee checkRefugee = refugeeService.getRefugeeByPhone(refugee.getPhoneNumber());
        if (checkRefugee != null)
            throw new CustomResponseStatusException("Refugee with this phone number already exists");

        final String passwordToken = CharSequenceGenerator.generatePasswordResetToken();
        refugee.getUser().setPassword(passwordToken);

        AccountStatus accountStatus = accountStatusService
                .saveAccountStatus(roleType == RoleType.Moderator ? AccountStatusType.Pending : AccountStatusType.Approved);

        if (accountStatus == null) {
            logService.logErrorMessage("AccountStatusService::createAccountStatus", refugee.getUser().getEmail());
            throw new InternalErrorResponseStatusException();
        }

        refugee.getUser().setAccountStatus(accountStatus);

        User savedUser = userService.createOrUpdateUser(refugee.getUser(), RoleType.Refugee);

        if (savedUser == null) {
            logService.logErrorMessage("UserService::createOrUpdateUser", refugee.getUser().getEmail());
            throw new InternalErrorResponseStatusException();
        }

        refugee.setUser(savedUser);

        if( roleType == RoleType.Administrator ){
            VerificationCode verificationCode = new VerificationCode();
            verificationCode.setVerificationCodeType(VerificationCodeType.NewAccount);
            verificationCode.setUser(savedUser);
            verificationCode.setCode(passwordToken);

            if( verificationCodeService.saveVerificationCode(verificationCode) == null ) {
                logService.logErrorMessage("VerificationCodeService::saveVerificationCode", "");
                throw new InternalErrorResponseStatusException();
            }
        }

        Address savedAddress = addressService.saveAddress(refugee.getAddress());
        if (savedAddress == null) {
            logService.logErrorMessage("AddressService::createAddress", refugee.getAddress().toString());
            throw new InternalErrorResponseStatusException();
        }

        refugee.setAddress(savedAddress);
        refugee.setId(0L);
        Refugee savedRefugee = refugeeService.saveRefugee(refugee);

        if (savedRefugee == null) {
            logService.logErrorMessage("RefugeeService::createRefugee", refugee.getUser().getEmail());
            throw new InternalErrorResponseStatusException();
        }

        if (!groupService.addRefugeeToGroups(refugee.getUser())) {
            logService.logErrorMessage("GroupService::addRefugeeToGroups", refugee.getUser().getEmail());
            throw new InternalErrorResponseStatusException();
        }

        Facility facility = facilityService.getById(refugee.getFacility().getId());
        if (facility == null) {
            logService.logErrorMessage("FacilityService::getById", refugee.getFacility().getId());
            throw new InternalErrorResponseStatusException();
        }

        facility.setCurrentCapacity(facility.getCurrentCapacity() + 1);
        if (facilityService.saveFacility(facility) == null) {
            logService.logErrorMessage("FacilityService::saveFacility", refugee.getFacility().getId());
            throw new InternalErrorResponseStatusException();
        }

        if( roleType == RoleType.Administrator ) {
            if (!mailService.sendNewUserEmail(savedUser, passwordToken)) {
                logService.logErrorMessage("MailService::sendResetPasswordEmail", "");
                throw new InternalErrorResponseStatusException();
            }
        }

        return new ResponseEntity<RoleType>(roleType, HttpStatus.OK);
    }

    @PostMapping("/validate-user-creation")
    public ResponseEntity<Void> validateUserCreation(@RequestBody User user) {

        user.setEmail(user.getEmail().toLowerCase(Locale.ROOT));

        validateUser(user);

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    private void validateUser(User user) {

        User currentUser = userService.getUserByEmail(user.getEmail());
        if (currentUser != null)
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
    @Transactional
    public ResponseEntity<Void> changePassword(@RequestBody AccountData accountData) {

        User user = userService.getUser(accountData.getId());
        if (user == null) {
            logService.logErrorMessage("UserService::getUser", accountData.getId());
            throw new InternalErrorResponseStatusException();
        }

        BCryptPasswordEncoderExtender encoder = new BCryptPasswordEncoderExtender();

        if( !accountData.isResetPasswordMode() ) {
            if (!encoder.matches(accountData.getOldPassword(), user.getPassword()))
                throw new CustomResponseStatusException("Old password is incorrect");
        }

        user.getAccountStatus().setLastPasswordChangeDate(LocalDate.now());

        AccountStatus accountStatus = accountStatusService.updateAccountStatus(user.getAccountStatus());
        if (accountStatus == null) {
            logService.logErrorMessage("AccountStatusService::updateAccountStatus", accountData.getId());
            throw new InternalErrorResponseStatusException();
        }

        user.setAccountStatus(accountStatus);
        user.setPassword(encoder.encode(accountData.getNewPassword()));
        if (userService.updateUser(user) == null) {
            logService.logErrorMessage("UserService::updateUser", accountData.getId());
            throw new InternalErrorResponseStatusException();
        }

        verificationCodeService.deleteVerificationCodes(user.getId());

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @PostMapping("/verify-user")
    @Transactional
    public ResponseEntity<User> verifyUser(@RequestBody UserSession userSession) {

        if (userSession.getId() == null || userSession.getAuthorizationToken() == null) {
            throw new UnauthorizedUserResponseStatusException();
        }

        long id = userSession.getId();
        String authorizationToken = userSession.getAuthorizationToken();

        userSession = userSessionService.getActiveUserSession(id);
        if (userSession == null) {
            throw new UnauthorizedUserResponseStatusException();
        }

        String decodedToken;
        try {
            decodedToken = new String(java.util.Base64.getDecoder().decode(authorizationToken));
        } catch (Exception e) {
            userSessionService.closeSessions(id);
            throw new UnauthorizedUserResponseStatusException();
        }

        BCryptPasswordEncoderExtender bCryptPasswordEncoderExtender = new BCryptPasswordEncoderExtender();
        if (!bCryptPasswordEncoderExtender.matches(decodedToken, userSession.getAuthorizationToken())) {
            userSessionService.closeSessions(id);
            throw new UnauthorizedUserResponseStatusException();
        }

        if (userSession.getExpirationDate().isBefore(LocalDateTime.now())) {
            userSessionService.closeSessions(id);
            throw new UnauthorizedUserResponseStatusException();
        }

        if (userSession.getExpirationDate().isBefore(LocalDateTime.now().plusMinutes(10))) {
            userSession.setExpirationDate(LocalDateTime.now().plusHours(1));
            userSessionService.saveUserSession(userSession);
        }

        return new ResponseEntity<User>(userSession.getUser(), HttpStatus.OK);
    }

    @GetMapping("/get-users/{id}")
    public List<User> getUsers(@PathVariable("id") Long id) {

        User user = userService.getUser(id);
        if (user == null) {
            logService.logErrorMessage("UserService::getUser", id);
            throw new InternalErrorResponseStatusException();
        }

        if (user.getRole().getRoleType() == RoleType.Administrator) {
            ArrayList<User> users = new ArrayList<User>();
            users = (ArrayList<User>) userService.getAll();

            return users;
        }

        if (user.getRole().getRoleType() == RoleType.Moderator) {
            return refugeeService.getRefugeesByResponsibleUser(user.getId());
        }

        return new ArrayList<User>();
    }

    @GetMapping("/get-users-filtered/{id}/{email}")
    public List<User> getUsers(@PathVariable("id") Long id, @PathVariable("email") String email) {

        email = email.toLowerCase(Locale.ROOT);

        User user = userService.getUser(id);
        if (user == null) {
            logService.logErrorMessage("UserService::getUser", id);
            throw new InternalErrorResponseStatusException();
        }

        if (user.getRole().getRoleType() == RoleType.Administrator) {
            ArrayList<User> users = new ArrayList<User>();
            users = (ArrayList<User>) userService.getAll(email);

            return users;
        }

        if (user.getRole().getRoleType() == RoleType.Moderator) {
            return refugeeService.getRefugeesByResponsibleUser(user.getId(), email);
        }

        return new ArrayList<User>();
    }

    @PostMapping("/update-user")
    @Transactional
    public ResponseEntity<User> updateUser(@RequestBody User user) {

        user.setEmail(user.getEmail().toLowerCase(Locale.ROOT));

        if (userService.checkEmailExists(user) != null) {
            throw new CustomResponseStatusException("User with this email already exists");
        }

        if (userService.checkIdentifierExists(user) != null) {
            throw new CustomResponseStatusException("User with this identifier already exists");
        }

        if (userService.updateUser(user) == null) {
            logService.logErrorMessage("UserService::updateUser", user.getId());
            throw new InternalErrorResponseStatusException();
        }

        return new ResponseEntity<User>(user, HttpStatus.OK);
    }

    @PutMapping("/change-status/{id}")
    public ResponseEntity<User> changeStatus(@PathVariable("id") Long id) {

        User user = userService.getUser(id);
        if (user == null) {
            logService.logErrorMessage("UserService::getUser", id);
            throw new InternalErrorResponseStatusException();
        }

        AccountStatus accountStatus = user.getAccountStatus();
        accountStatus.setAccountStatusType(accountStatus.getAccountStatusType() == AccountStatusType.Blocked ?
                AccountStatusType.Verified
                : AccountStatusType.Blocked);

        userSessionService.closeSessions(id);

        accountStatus = accountStatusService.updateAccountStatus(accountStatus);
        if (accountStatus == null) {
            logService.logErrorMessage("AccountStatusService::updateAccountStatus", id);
            throw new InternalErrorResponseStatusException();
        }

        user.setAccountStatus(accountStatus);
        return new ResponseEntity<User>(user, HttpStatus.OK);
    }
}
