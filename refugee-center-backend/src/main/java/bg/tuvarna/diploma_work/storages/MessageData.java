package bg.tuvarna.diploma_work.storages;

import bg.tuvarna.diploma_work.models.Message;

import java.io.Serializable;
import java.util.List;

public class MessageData implements Serializable {

    private Message message;

    private List<MailReceiver> receivers;

    public MessageData() {
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public List<MailReceiver> getReceivers() {
        return receivers;
    }

    public void setReceivers(List<MailReceiver> receivers) {
        this.receivers = receivers;
    }

}
