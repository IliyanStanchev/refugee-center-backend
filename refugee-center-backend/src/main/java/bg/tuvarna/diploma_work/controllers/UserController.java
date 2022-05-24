package bg.tuvarna.diploma_work.controllers;

import bg.tuvarna.diploma_work.enumerables.RoleType;
import bg.tuvarna.diploma_work.exceptions.CustomResponseStatusException;
import bg.tuvarna.diploma_work.exceptions.InternalErrorResponseStatusException;
import bg.tuvarna.diploma_work.models.Address;
import bg.tuvarna.diploma_work.models.Refugee;
import bg.tuvarna.diploma_work.models.User;
import bg.tuvarna.diploma_work.services.*;
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

    @PostMapping("/authenticate-user")
    public User authenticateUser(@RequestBody User user) {
        return userService.authenticateUser(user);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> sendNewPassword(@RequestBody User user) {

        User currentUser = userService.getUserByEmail(user.getEmail());
        if( currentUser == null )
            return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);

        final String newPassword = PasswordGeneratorUtil.generatePassword();

        User modifiedUser = userService.changePassword(currentUser, newPassword);

        if( modifiedUser == null )
        {
            LogService.logErrorMessage("UserService::changePassword", modifiedUser.getEmail(), modifiedUser.getId());
            return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        User databaseUser = userService.getUser(modifiedUser.getId());
        if( databaseUser == null )
        {
            LogService.logErrorMessage("CustomerService::getCustomerByUserID", String.valueOf(modifiedUser.getId()), modifiedUser.getId());
            return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if( !mailService.sendResetPasswordEmail(databaseUser, newPassword) )
        {
            LogService.logErrorMessage("MailService::sendResetPasswordEmail", "", modifiedUser.getId());
            return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @PostMapping("/create-employee")
    public ResponseEntity<Void> createEmployee(@RequestBody User user) {

        user.setEmail(user.getEmail().toLowerCase(Locale.ROOT));

        validateUser(user);

        final String newPassword = PasswordGeneratorUtil.generatePassword();
        user.setPassword(newPassword);
        user.setCreatedOn(LocalDate.now());

        User savedUser = userService.createOrUpdateUser( user, RoleType.MODERATOR );

        if( savedUser == null )
        {
            LogService.logErrorMessage("UserService::createOrUpdateUser", user.getEmail() );
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
    public ResponseEntity<Void> createEmployee(@RequestBody Refugee refugee) {

        refugee.getUser().setEmail(refugee.getUser().getEmail().toLowerCase(Locale.ROOT));

        validateUser(refugee.getUser());

        final String newPassword = PasswordGeneratorUtil.generatePassword();
        refugee.getUser().setPassword(newPassword);
        refugee.getUser().setCreatedOn(LocalDate.now());

        User savedUser = userService.createOrUpdateUser( refugee.getUser(), RoleType.CUSTOMER );

        if( savedUser == null ) {
            LogService.logErrorMessage("UserService::createOrUpdateUser", refugee.getUser().getEmail() );
            throw new InternalErrorResponseStatusException();
        }

        refugee.setUser(savedUser);

       Address savedAddress = addressService.createAddress(refugee.getAddress());
        if( savedAddress == null ) {
            LogService.logErrorMessage("AddressService::createAddress", refugee.getAddress().toString());
            return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        refugee.setAddress(savedAddress);

        Refugee savedRefugee = refugeeService.createRefugee( refugee );

        if( savedRefugee == null ){
            LogService.logErrorMessage("RefugeeService::createRefugee", refugee.getUser().getEmail() );
            return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if( !mailService.sendNewUserEmail(savedUser, newPassword) )
        {
            LogService.logErrorMessage("MailService::sendResetPasswordEmail", "", refugee.getId());
            return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @PostMapping("/validate-user-creation")
    public ResponseEntity<Void> validateUserCreation(@RequestBody User user) {

        user.setEmail(user.getEmail().toLowerCase(Locale.ROOT));

        validateUser(user);

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @GetMapping("{pathname}/users")
    public List<User> getAllUsers(@PathVariable String pathname) {
        return userService.getAll();
    }

    @GetMapping("/{pathname}/users/{id}")
    public User getUser(@PathVariable String pathname, @PathVariable long id) {

        return userService.getUser(id);
    }

    @DeleteMapping("/{pathname}/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String pathname, @PathVariable long id) {

        userService.deleteUser(id);

        User user = userService.getUser(id);

        return user == null ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();

    }

    @PutMapping("/{pathname}/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable String pathname, @PathVariable long id,
                                           @RequestBody User user) {

        User updatedUser = userService.createOrUpdateUser(user, RoleType.CUSTOMER);

        return new ResponseEntity<User>(updatedUser, HttpStatus.OK);
    }

    @PostMapping("/{pathname}/login")
    public ResponseEntity<Void> createUser(@PathVariable String pathname, @RequestBody User requestUser) {

        User user = userService.getUserByEmail(requestUser.getEmail());
        if (user != null)
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);

        requestUser = userService.createOrUpdateUser(requestUser, RoleType.CUSTOMER);

        if (requestUser == null)
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    private void validateUser( User user ){

        User currentUser = userService.getUserByEmail(user.getEmail());
        if( currentUser != null )
            throw new CustomResponseStatusException("User with this email already exists");

        currentUser = userService.getUserByIdentifier(user.getIdentifier());
        if( currentUser != null )
            throw new CustomResponseStatusException("User with this identifier already exists");
    }
}
