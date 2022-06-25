package bg.tuvarna.diploma_work.services;

import bg.tuvarna.diploma_work.enumerables.RoleType;
import bg.tuvarna.diploma_work.models.Role;
import bg.tuvarna.diploma_work.models.User;
import bg.tuvarna.diploma_work.repositories.RoleRepository;
import bg.tuvarna.diploma_work.repositories.UserRepository;
import bg.tuvarna.diploma_work.security.BCryptPasswordEncoderExtender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private MailService mailService;

    public List<User> getAll() {

        return  (List<User>) userRepository.getUsersByRole( RoleType.Refugee);
    }

    public User getUser(long id ) {
        Optional<User> user = userRepository.findById( id );

        return user.isPresent() ? user.get() : null;
    }

    public User createOrUpdateUser( User modifiedUser, RoleType roleType ) {

        BCryptPasswordEncoderExtender bCryptPasswordEncoder = new BCryptPasswordEncoderExtender();
        modifiedUser.setPassword( bCryptPasswordEncoder.encode( modifiedUser.getPassword() ));

        if( modifiedUser.getId() == null )
            modifiedUser.setId(0L);

        Optional<User> user = userRepository.findById( modifiedUser.getId() );
        return user.isPresent() ? updateUser( modifiedUser, user.get() ) : saveUser( modifiedUser, roleType );

    }

    public void deleteUser( long id ) {
        Optional<User> user = userRepository.findById(id);

        if ( user.isPresent() )
            userRepository.deleteById(id);
    }

    public User getUserByEmail(String email) {
        return userRepository.getUserByEmail(email);
    }

    private User updateUser( User newBuffer, User oldBuffer ){

        oldBuffer.setNewValues( newBuffer );
        return userRepository.save( oldBuffer );
    }

    private User saveUser( User user, RoleType roleType ){

        Role role = roleRepository.getRole( roleType );

        if( role == null )
            return null;

        user.setRole( role );
        return userRepository.save( user );
    }

    public User authenticateUser(User user) {


        User currentUser = userRepository.getUserByEmail(user.getEmail());

        if (currentUser == null)
            return null;

        BCryptPasswordEncoderExtender bCryptPasswordEncoderExtender = new BCryptPasswordEncoderExtender();

        if (!bCryptPasswordEncoderExtender.matches(user.getPassword(), currentUser.getPassword()))
            return null;

        return currentUser;
    }

    public User changePassword(User currentUser, String newPassword) {

        BCryptPasswordEncoderExtender bCryptPasswordEncoderExtender = new BCryptPasswordEncoderExtender();
        currentUser.setPassword(bCryptPasswordEncoderExtender.encode(newPassword));

        return userRepository.save(currentUser);
    }

    public User getUserByIdentifier(String identifier) {
        return userRepository.getUserByIdentifier(identifier);
    }

    public List<User> getResponsibleUsers() {

        return userRepository.getResponsibleUsers();
    }

    public User updateUser(User user) {
    	return userRepository.save(user);
    }
}
