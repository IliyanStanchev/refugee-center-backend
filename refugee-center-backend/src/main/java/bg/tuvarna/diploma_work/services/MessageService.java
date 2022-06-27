package bg.tuvarna.diploma_work.services;

import bg.tuvarna.diploma_work.enumerables.MessageType;
import bg.tuvarna.diploma_work.models.*;
import bg.tuvarna.diploma_work.repositories.GroupRepository;
import bg.tuvarna.diploma_work.repositories.MessageRepository;
import bg.tuvarna.diploma_work.repositories.UserMessageRepository;
import bg.tuvarna.diploma_work.repositories.UserRepository;
import bg.tuvarna.diploma_work.storages.MailReceiver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

    @Autowired
    private LogService logService;


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

        for (User user : users) {
            mailReceivers.add(new MailReceiver(user));
        }

        for (Group group : groups) {
            mailReceivers.add(new MailReceiver(group));
        }

        return mailReceivers;
    }

    public List<MailReceiver> getMessageReceivers(long messageId) {

        List<UserMessage> userMessages = userMessageRepository.getMessageReceivers(messageId);
        List<MailReceiver> mailReceivers = new ArrayList<>();

        for (UserMessage userMessage : userMessages) {
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

    public void notifyDonationOutOfStock(Donation donation) {

        Message message = new Message();
        message.setDateReceived(LocalDate.now());

        message.setSubject("Donation out of stock");
        message.setMessageType(MessageType.Important);
        message.setContent(donation.getName() + " is out of stock");

        notifyDonationOutOfStock(message);
    }

    public void notifyDonationOutOfStock(Donation donation, int daysTillOutOfStock) {

        Message message = new Message();
        message.setDateReceived(LocalDate.now());

        message.setSubject("Donation stock is low");
        message.setMessageType(MessageType.Informative);
        message.setContent(donation.getName() + " stock is low. It will be out of stock in " + daysTillOutOfStock + " days");

        notifyDonationOutOfStock(message);
    }

    private void notifyDonationOutOfStock(Message message) {

        List<User> users = userRepository.getResponsibleUsers();

        if (users.size() < 0)
            return;

        User sender = userRepository.getSystemUser();
        if (sender == null) {
            logService.logErrorMessage("UserRepository::getSystemUser", "System user not found");
            return;
        }
        message.setSender(sender);

        Message savedMessage = messageRepository.save(message);
        if (savedMessage == null) {
            logService.logErrorMessage("MessageRepository::saveMessage", sender.getId());
            return;
        }

        for (User receiver : users) {

            if (userMessageRepository.save(new UserMessage(savedMessage, receiver)) == null) {
                logService.logErrorMessage("UserMessageRepository::save", sender.getId());
                return;
            }
        }
    }
}
