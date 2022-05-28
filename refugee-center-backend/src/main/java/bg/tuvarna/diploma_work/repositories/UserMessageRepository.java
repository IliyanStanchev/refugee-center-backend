package bg.tuvarna.diploma_work.repositories;

import bg.tuvarna.diploma_work.models.UserMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserMessageRepository extends JpaRepository<UserMessage, Long> {

    @Query(value="SELECT DISTINCT ON (um.message_id) um.* FROM users_messages um JOIN messages m ON m.id = um.message_id JOIN users u ON m.sender_id = u.id WHERE u.id = ?1", nativeQuery = true)
    List<UserMessage> getSendMessages(long userId);

    @Query("SELECT u FROM UserMessage u WHERE u.receiver.id = ?1 ORDER BY u.seen, u.message.dateReceived")
    List<UserMessage> getReceivedMessages(long id);

    @Query("SELECT u FROM UserMessage u WHERE u.message.id = ?1")
    List<UserMessage> getMessageReceivers(long messageId);

    @Modifying
    @Query("UPDATE UserMessage u SET u.seen = true WHERE u.id IN ( :messages )")
    void markMessagesAsRead(@Param("messages") List<Long> messages );

    @Modifying
    @Query("DELETE FROM UserMessage u WHERE u.id IN ( :messages )")
    void deleteMessages(List<Long> messages);

    @Modifying
    @Query("UPDATE UserMessage u SET u.seen = true WHERE u.id = ?1")
    void setAsSeen(long userMessageId);


}
