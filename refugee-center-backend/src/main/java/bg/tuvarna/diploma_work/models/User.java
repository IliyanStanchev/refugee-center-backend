package bg.tuvarna.diploma_work.models;

import org.apache.tomcat.jni.Local;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "USERS")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition ="serial")
    private Long id;

    private String email;
    private String password;

    private String firstName;
    private String lastName;
    private String identifier;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @OneToOne
    private AccountStatus accountStatus;

    public User() {
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, password, firstName, lastName, identifier, role);
    }

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setNewValues( User newBuffer ) {
        setPassword(newBuffer.getPassword());
        setEmail(newBuffer.getEmail());
        setFirstName(newBuffer.getFirstName());
        setLastName(newBuffer.getLastName());
        setIdentifier(newBuffer.getIdentifier());
    }

    public String getName() {
        return firstName + " " + lastName;
    }
}
