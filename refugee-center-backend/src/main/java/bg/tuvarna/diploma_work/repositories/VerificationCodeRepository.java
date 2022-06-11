package bg.tuvarna.diploma_work.repositories;

import bg.tuvarna.diploma_work.models.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {

    @Modifying
    @Query("DELETE FROM VerificationCode vc WHERE vc.user.id = ?1")
    void deleteVerificationCodes(Long userId);

    @Query("SELECT vc FROM VerificationCode vc WHERE vc.user.id = ?1")
    VerificationCode getVerificationCode(Long id);

}

