package bg.tuvarna.diploma_work.services;

import bg.tuvarna.diploma_work.models.Message;
import bg.tuvarna.diploma_work.repositories.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.Access;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;
}
