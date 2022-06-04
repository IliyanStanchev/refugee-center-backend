package bg.tuvarna.diploma_work.repositories;

import bg.tuvarna.diploma_work.models.LocationChangeRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LocationChangeRequestRepository extends JpaRepository<LocationChangeRequest , Long> {


    @Query("SELECT l FROM LocationChangeRequest l WHERE l.requestStatus = 0 AND l.refugee.id = ?1")
    LocationChangeRequest checkForPendingLocationChangeRequest(Long refugeeId);
}
