package bg.tuvarna.diploma_work.services;

import bg.tuvarna.diploma_work.models.UserSession;
import bg.tuvarna.diploma_work.repositories.UserSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserSessionService {

    @Autowired
    private UserSessionRepository userSessionRepository;

    public UserSession saveUserSession(UserSession userSession) {

        return userSessionRepository.save(userSession);
    }

    public void closeSessions(Long id) {

        userSessionRepository.closeSessions(id);
    }

    public UserSession getActiveUserSession(long userId) {

        return userSessionRepository.getActiveUserSession(userId);
    }
}
