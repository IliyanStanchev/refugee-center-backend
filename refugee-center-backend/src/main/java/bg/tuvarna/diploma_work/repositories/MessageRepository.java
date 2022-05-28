package bg.tuvarna.diploma_work.repositories;

import bg.tuvarna.diploma_work.models.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {


}