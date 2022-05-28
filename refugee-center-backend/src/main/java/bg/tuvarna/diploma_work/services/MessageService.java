package bg.tuvarna.diploma_work.services;

import bg.tuvarna.diploma_work.models.Group;
import bg.tuvarna.diploma_work.models.Message;
import bg.tuvarna.diploma_work.models.User;
import bg.tuvarna.diploma_work.models.UserMessage;
import bg.tuvarna.diploma_work.repositories.GroupRepository;
import bg.tuvarna.diploma_work.repositories.MessageRepository;
import bg.tuvarna.diploma_work.repositories.UserMessageRepository;
import bg.tuvarna.diploma_work.repositories.UserRepository;
import bg.tuvarna.diploma_work.storages.MailReceiver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserMessageRepository userMessageRepository;


    public List<UserMessage> getSendMessages(long userId) {

        return userMessageRepository.getSendMessages(userId);
    }

    public void markMessagesAsRead(List<Long> messages) {

        userMessageRepository.markMessagesAsRead(messages);
    }

    public void deleteMessages(List<Long> messages) {

        userMessageRepository.deleteMessages(messages);
    }

    public List<MailReceiver> getReceivers() {

        List<User> users = userRepository.findAll();
        List<Group> groups = groupRepository.findAll();

        List<MailReceiver> mailReceivers = new ArrayList<>();

        for( User user : users ){
            mailReceivers.add(new MailReceiver(user));
        }

        for( Group group : groups ){
            mailReceivers.add( new MailReceiver(group));
        }

       return mailReceivers;
    }

    public List<MailReceiver> getMessageReceivers(long messageId) {

       List<UserMessage> userMessages = userMessageRepository.getMessageReceivers(messageId);
       List<MailReceiver> mailReceivers = new ArrayList<>();

       for( UserMessage userMessage : userMessages ){
           mailReceivers.add(new MailReceiver(userMessage.getReceiver()));
       }

       return mailReceivers;
    }

    public Message saveMessage(Message message) {

        return messageRepository.save(message);
    }

    public UserMessage sendMessage(UserMessage userMessage) {

        return userMessageRepository.save(userMessage);
    }

    public void setAsSeen(long userMessageId) {

        userMessageRepository.setAsSeen(userMessageId);
    }

    public List<UserMessage> getReceivedMessages(long id) {

        return userMessageRepository.getReceivedMessages(id);
    }
}
