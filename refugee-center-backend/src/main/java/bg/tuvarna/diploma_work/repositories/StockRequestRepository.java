package bg.tuvarna.diploma_work.repositories;

import bg.tuvarna.diploma_work.models.StockRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StockRequestRepository extends JpaRepository<StockRequest, Long> {

    @Query("SELECT s FROM StockRequest s WHERE s.refugee.user.id = ?1 ORDER BY s.requestStatus, s.dateCreated DESC")
    List<StockRequest> getStockRequests(Long userId);

    @Query("SELECT s FROM StockRequest s WHERE s.refugee.facility.responsibleUser.id = ?1 AND s.requestStatus = 0 ORDER BY s.dateCreated DESC")
    List<StockRequest> getPendingStockRequests(Long userId);
}
