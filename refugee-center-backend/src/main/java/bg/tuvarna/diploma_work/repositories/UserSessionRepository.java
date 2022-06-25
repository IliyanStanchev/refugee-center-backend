package bg.tuvarna.diploma_work.repositories;

import bg.tuvarna.diploma_work.models.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {

    @Modifying
    @Query("UPDATE UserSession u SET u.activeSession = false WHERE u.user.id = ?1")
    void closeSessions(Long id);

    @Query("SELECT u FROM UserSession u WHERE u.user.id = ?1 and u.activeSession = true")
    UserSession getActiveUserSession(long userId);
}
