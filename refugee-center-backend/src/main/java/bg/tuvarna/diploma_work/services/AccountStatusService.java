package bg.tuvarna.diploma_work.services;

import bg.tuvarna.diploma_work.enumerables.AccountStatusType;
import bg.tuvarna.diploma_work.models.AccountStatus;
import bg.tuvarna.diploma_work.repositories.AccountStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class AccountStatusService {

    @Autowired
    private AccountStatusRepository accountStatusRepository;

    public AccountStatus saveAccountStatus(AccountStatusType accountStatusType) {

        AccountStatus accountStatus = new AccountStatus();
        accountStatus.setCreatedOn(LocalDate.now());
        accountStatus.setLastPasswordChangeDate(LocalDate.now());
        accountStatus.setAccountStatusType(accountStatusType);

        return accountStatusRepository.save(accountStatus);
    }

    public AccountStatus updateAccountStatus(AccountStatus accountStatus) {
        return accountStatusRepository.save(accountStatus);
    }
}
