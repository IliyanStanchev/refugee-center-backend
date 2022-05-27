package bg.tuvarna.diploma_work.models;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "USERS_GROUPS")
public class UserGroup implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "serial")
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Group group;

    public UserGroup() {
    }

    public UserGroup(User user, Group group) {
        id = 0L;
        this.user   = user;
        this.group  = group;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        UserGroup userGroup = (UserGroup) o;
        return Objects.equals(id, userGroup.id) && Objects.equals(user, userGroup.user) && Objects.equals(group, userGroup.group);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, group);
    }
}

