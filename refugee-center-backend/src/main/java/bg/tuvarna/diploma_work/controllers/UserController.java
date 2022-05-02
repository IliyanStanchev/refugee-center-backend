package bg.tuvarna.diploma_work.controllers;

import bg.tuvarna.diploma_work.enumerables.RoleType;
import bg.tuvarna.diploma_work.models.Customer;
import bg.tuvarna.diploma_work.models.User;
import bg.tuvarna.diploma_work.services.*;
import bg.tuvarna.diploma_work.utils.PasswordGeneratorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = {"http://localhost:3000"})
@RestController
public class UserController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private UserService userService;

    @Autowired
    private MailService mailService;

    @Autowired
    private RoleService roleService;

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

        Customer customer = customerService.getCustomerByUserID(modifiedUser.getId());
        if( customer == null )
        {
            LogService.logErrorMessage("CustomerService::getCustomerByUserID", String.valueOf(modifiedUser.getId()), modifiedUser.getId());
            return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if( !mailService.sendResetPasswordEmail(customer, newPassword) )
        {
            LogService.logErrorMessage("MailService::sendResetPasswordEmail", "", modifiedUser.getId());
            return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

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
}
