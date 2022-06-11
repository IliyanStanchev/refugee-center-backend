package bg.tuvarna.diploma_work.schedulers;

import bg.tuvarna.diploma_work.services.VerificationCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class VerificationCodeScheduler {

    @Autowired
    VerificationCodeService verificationCodeService;

    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void removeExpiredVerificationCodes() {

        verificationCodeService.removeExpiredVerificationCodes();
    }
}
