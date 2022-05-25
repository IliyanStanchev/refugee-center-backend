package bg.tuvarna.diploma_work.repositories;

import bg.tuvarna.diploma_work.models.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountStatusRepository extends JpaRepository<AccountStatus, Long> {
}
