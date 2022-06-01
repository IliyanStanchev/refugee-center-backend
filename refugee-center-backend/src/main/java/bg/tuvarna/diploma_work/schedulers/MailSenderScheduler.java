package bg.tuvarna.diploma_work.schedulers;

import bg.tuvarna.diploma_work.models.MailMessage;
import bg.tuvarna.diploma_work.services.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class MailSenderScheduler {

    @Autowired
    MailService mailService;

    @Scheduled(fixedDelay = 30000)
    @Transactional
    public void processPendingMails() {

        final long threadId = Thread.currentThread().getId();
        mailService.reserveMailMessages(threadId);

        List<MailMessage> mailMessages = mailService.getPendingMails(threadId);

        for (MailMessage mailMessage : mailMessages) {

            if( !mailService.sendEmail(mailMessage) )
            {
                mailMessage.setThreadId(0L);
                mailService.updateMailMessage( mailMessage );
                continue;
            }
        }

        mailService.deleteMailMessages(threadId);
    }
}
