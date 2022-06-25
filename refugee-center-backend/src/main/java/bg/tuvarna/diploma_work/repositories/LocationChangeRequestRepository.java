package bg.tuvarna.diploma_work.repositories;

import bg.tuvarna.diploma_work.models.LocationChangeRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LocationChangeRequestRepository extends JpaRepository<LocationChangeRequest , Long> {


    @Query("SELECT l FROM LocationChangeRequest l WHERE l.requestStatus = 0 AND l.refugee.id = ?1")
    LocationChangeRequest checkForPendingLocationChangeRequest(Long refugeeId);

    @Query("SELECT l FROM LocationChangeRequest l WHERE l.refugee.user.id = ?1 ORDER BY l.requestStatus, l.dateCreated DESC")
    List<LocationChangeRequest> getLocationChangeRequests(Long userId);

    @Query("SELECT l FROM LocationChangeRequest l WHERE l.refugee.facility.responsibleUser.id = ?1 AND l.requestStatus = 0 ORDER BY l.dateCreated DESC")
    List<LocationChangeRequest> getPendingLocationChangeRequests(Long userId);
}
