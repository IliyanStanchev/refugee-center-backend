package bg.tuvarna.diploma_work.models;

import bg.tuvarna.diploma_work.enumerables.MessageType;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "MESSAGES")
public class Message implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "serial")
    private Long id;

    @OneToOne
    private User sender;

    @Column(nullable = false)
    private MessageType messageType;

    @Column(nullable = false, length = 64)
    private String subject;

    @Column(nullable = false, length = 256)
    private String content;

    @Column(nullable = false)
    private LocalDate dateReceived;

    public Message() {
    }

    public Message(Long id, User sender, MessageType messageType, String subject, String content) {
        this.id = id;
        this.sender = sender;
        this.messageType = messageType;
        this.subject = subject;
        this.content = content;
    }

    public LocalDate getDateReceived() {
        return dateReceived;
    }

    public void setDateReceived(LocalDate dateReceived) {
        this.dateReceived = dateReceived;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(id, message.id) && Objects.equals(sender, message.sender) && messageType == message.messageType && Objects.equals(subject, message.subject) && Objects.equals(content, message.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sender, messageType, subject, content);
    }
}

