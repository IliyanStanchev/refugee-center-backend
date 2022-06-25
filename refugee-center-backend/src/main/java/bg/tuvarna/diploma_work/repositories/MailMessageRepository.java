package bg.tuvarna.diploma_work.repositories;

import bg.tuvarna.diploma_work.models.MailMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.LockModeType;
import java.util.List;

public interface MailMessageRepository extends JpaRepository<MailMessage, Long> {

    @Query("SELECT m FROM MailMessage m WHERE m.threadId = ?1 ORDER BY m.unsuccessfulAttempts DESC")
    List<MailMessage> getPendingMails(long threadId);

    @Modifying
    @Query(value="UPDATE mail_messages m SET thread_id = ?1 WHERE id IN ( SELECT id FROM mail_messages WHERE thread_id = 0 LIMIT 100 )", nativeQuery = true)
    void reserveMailMessages(long threadId);

    @Modifying
    @Query("DELETE FROM MailMessage m WHERE m.threadId = ?1 OR m.unsuccessfulAttempts > 10")
    void deleteMailMessages(long threadId);
}
