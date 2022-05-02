package bg.tuvarna.diploma_work.repositories;

import bg.tuvarna.diploma_work.models.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogRepository extends JpaRepository<Log, Long> {

}
