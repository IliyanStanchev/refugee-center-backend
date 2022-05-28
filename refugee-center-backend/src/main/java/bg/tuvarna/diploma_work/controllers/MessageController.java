package bg.tuvarna.diploma_work.controllers;

import bg.tuvarna.diploma_work.exceptions.InternalErrorResponseStatusException;
import bg.tuvarna.diploma_work.models.Message;
import bg.tuvarna.diploma_work.models.User;
import bg.tuvarna.diploma_work.models.UserMessage;
import bg.tuvarna.diploma_work.services.GroupService;
import bg.tuvarna.diploma_work.services.LogService;
import bg.tuvarna.diploma_work.services.MessageService;
import bg.tuvarna.diploma_work.services.UserService;
import bg.tuvarna.diploma_work.storages.MailReceiver;
import bg.tuvarna.diploma_work.storages.MessageData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@CrossOrigin(origins = {"http://localhost:3000"})
@RestController
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private UserService userService;

    @GetMapping("/get-send-messages/{id}")
    public List<UserMessage> getSendMessages(@PathVariable long id){

        return messageService.getSendMessages(id);
    }

    @GetMapping("/get-received-messages/{id}")
    public List<UserMessage> getReceivedMessages(@PathVariable long id){

        return messageService.getReceivedMessages(id);
    }

    @GetMapping("get-receivers")
    public List<MailReceiver> getReceivers(){

        return messageService.getReceivers();
    }

    @GetMapping("/get-message-receivers/{id}")
    public List<MailReceiver> getMessageReceivers(@PathVariable long id){

        return messageService.getMessageReceivers(id);
    }

    @PostMapping("/mark-messages-as-read")
    @Transactional
    public ResponseEntity<Void> markMessagesAsRead(@RequestBody List<Long> messages) {

        messageService.markMessagesAsRead(messages);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/delete-messages")
    @Transactional
    public ResponseEntity<Void> deleteMessages(@RequestBody List<Long> messages) {

        messageService.deleteMessages(messages);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/send-message")
    @Transactional
    public ResponseEntity<Void> sendMessage(@RequestBody MessageData messageData) {

        Set<User> receivers = new HashSet<>();

        for( MailReceiver mailReceiver : messageData.getReceivers() ){

            if( mailReceiver.getIsUser() == true ) {
                receivers.add(mailReceiver.getUser());
                continue;
            }

            List<User> groupUsers = groupService.getGroupUsers(mailReceiver.getGroup().getId());
            for( User user : groupUsers )
                receivers.add( user );

        }

        if( receivers.size() > 0 )
            return new ResponseEntity<>(HttpStatus.OK);

        final long senderId = messageData.getMessage().getSender().getId();

        User sender = userService.getUser(senderId);
        if( sender == null )
        {
            LogService.logErrorMessage("UserService::getUser", senderId );
            throw new InternalErrorResponseStatusException();
        }

        messageData.getMessage().setSender(sender);
        Message savedMessage = messageService.saveMessage(messageData.getMessage());
        if( savedMessage == null )
        {
            LogService.logErrorMessage("MessageService::saveMessage", senderId );
            throw new InternalErrorResponseStatusException();
        }

        for( User receiver : receivers ){

            if( messageService.sendMessage(new UserMessage(savedMessage, receiver)) == null )
            {
                LogService.logErrorMessage("MessageService::sendMessage", senderId );
                throw new InternalErrorResponseStatusException();
            }
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/set-as-seen/{id}")
    @Transactional
    public void setAsSeen(@PathVariable long id){

        messageService.setAsSeen(id);
    }
}