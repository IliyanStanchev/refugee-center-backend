package bg.tuvarna.diploma_work.models;

import bg.tuvarna.diploma_work.enumerables.AccountStatusType;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "ACCOUNT_STATUSES")
public class AccountStatus implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "serial")
    private Long id;

    private LocalDate createdOn;

    private LocalDate lastLogin;

    private LocalDate lastPasswordChangeDate;

    private AccountStatusType accountStatusType;

    public AccountStatus() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDate createdOn) {
        this.createdOn = createdOn;
    }

    public LocalDate getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDate lastLogin) {
        this.lastLogin = lastLogin;
    }

    public LocalDate getLastPasswordChangeDate() {
        return lastPasswordChangeDate;
    }

    public void setLastPasswordChangeDate(LocalDate lastPasswordChangeDate) {
        this.lastPasswordChangeDate = lastPasswordChangeDate;
    }

    public AccountStatusType getAccountStatusType() {
        return accountStatusType;
    }

    public void setAccountStatusType(AccountStatusType accountStatusType) {
        this.accountStatusType = accountStatusType;
    }
}
