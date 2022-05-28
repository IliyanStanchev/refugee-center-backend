package bg.tuvarna.diploma_work.storages;

import bg.tuvarna.diploma_work.models.Group;
import bg.tuvarna.diploma_work.models.User;

import java.io.Serializable;
import java.util.Objects;

public class MailReceiver implements Serializable {

    private boolean isUser;

    private String email;

    private User user;

    private Group group;

    public MailReceiver() {
    }

    public MailReceiver(User user) {
        this.user = user;
        this.email = user.getEmail();
        this.isUser = true;
    }

    public MailReceiver(Group group) {
        this.group = group;
        this.email = group.getEmail();
        this.isUser = false;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean getIsUser() {
        return isUser;
    }

    public void setIsUser(boolean user) {
        isUser = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MailReceiver that = (MailReceiver) o;
        return isUser == that.isUser && Objects.equals(user, that.user) && Objects.equals(group, that.group);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isUser, user, group);
    }
}
