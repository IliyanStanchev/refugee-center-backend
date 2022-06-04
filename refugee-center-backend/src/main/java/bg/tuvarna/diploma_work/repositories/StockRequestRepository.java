package bg.tuvarna.diploma_work.repositories;

import bg.tuvarna.diploma_work.models.StockRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRequestRepository extends JpaRepository<StockRequest, Long> {
}
