package bg.tuvarna.diploma_work.storages;

import bg.tuvarna.diploma_work.enumerables.MessageType;

import java.io.Serializable;
import java.util.Objects;

public class MailMessage implements Serializable {

    private String sender;

    private String receiver;

    private String subject;

    private String content;

    private MessageType messageType;

    public MailMessage(){

    }

    public MailMessage(String from, String to, String subject, String content, MessageType messageType) {
        this.sender = from;
        this.receiver = to;
        this.subject = subject;
        this.content = content;
        this.messageType = messageType;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String from) {
        this.sender = from;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
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

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MailMessage message = (MailMessage) o;
        return Objects.equals(sender, message.sender) && Objects.equals(receiver, message.receiver) && Objects.equals(subject, message.subject) && Objects.equals(content, message.content) && messageType == message.messageType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sender, receiver, subject, content, messageType);
    }

    @Override
    public String toString() {
        return "Message{" +
                "sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", subject='" + subject + '\'' +
                ", content='" + content + '\'' +
                ", messageType=" + messageType +
                '}';
    }
}
