package bg.tuvarna.diploma_work.repositories;

import bg.tuvarna.diploma_work.models.MedicalHelpRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MedicalHelpRequestRepository extends JpaRepository<MedicalHelpRequest , Long> {

    @Query("SELECT m FROM MedicalHelpRequest m WHERE m.refugee.user.id = ?1 ORDER BY m.dateCreated DESC")
    List<MedicalHelpRequest> getMedicalHelpRequests(Long id);
}
