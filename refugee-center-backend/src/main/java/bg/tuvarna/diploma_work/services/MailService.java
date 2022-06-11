package bg.tuvarna.diploma_work.services;

import bg.tuvarna.diploma_work.helpers.EmailTemplates;
import bg.tuvarna.diploma_work.models.MailMessage;
import bg.tuvarna.diploma_work.models.Refugee;
import bg.tuvarna.diploma_work.models.User;
import bg.tuvarna.diploma_work.repositories.MailMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.List;

@Service
public class MailService {

    @Autowired
    private Environment environment;

    @Autowired
    private MailMessageRepository mailMessageRepository;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private LogService logService;

    public boolean sendEmail(MailMessage message) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {

            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

            mimeMessageHelper.setFrom(new InternetAddress(message.getSender()));
            mimeMessageHelper.setTo(message.getReceiver());

            mimeMessageHelper.setSubject(message.getSubject());
            mimeMessageHelper.setText(message.getContent(),true);

            javaMailSender.send(mimeMessageHelper.getMimeMessage());

        }
        catch (MessagingException e) {
            logService.logErrorMessage("sendEmail", message.toString());
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean sendResetPasswordEmail(User user, String newPassword) {

        MailMessage message = new MailMessage();

        message.setSender(environment.getProperty("spring.mail.username"));
        message.setReceiver(user.getEmail());

        message.setSubject("Password reset");

        final String messageContent = EmailTemplates.getResetPasswordTemplate()
                .replace("{NAME}", user.getName())
                .replace("{PASSWORD}", newPassword );

        message.setContent(messageContent);

        if(!sendEmail(message))
            return false;

        return true;
    }

    public boolean sendNewUserEmail(User user, String newPassword) {

        MailMessage message = new MailMessage();

        message.setSender(environment.getProperty("spring.mail.username"));
        message.setReceiver(user.getEmail());

        message.setSubject("Welcome");

        final String messageContent = EmailTemplates.getNewUserTemplate()
                .replace("{NAME}", user.getName())
                .replace("{PASSWORD}", newPassword );

        message.setContent(messageContent);

        if(!sendEmail(message))
            return false;

        return true;
    }

    public boolean sendDeclinedRegistrationEmail(User user) {

        MailMessage mailMessage = new MailMessage();

        mailMessage.setSender(environment.getProperty("spring.mail.username"));
        mailMessage.setReceiver(user.getEmail());

        mailMessage.setSubject("Declined registration");

        final String messageContent = EmailTemplates.getDeclinedRegistrationTemplate()
                .replace("{NAME}", user.getName());

        mailMessage.setContent(messageContent);

        mailMessageRepository.save(mailMessage);

        return true;
    }

    public boolean sendNewNotificationEmail(User user) {

        MailMessage mailMessage = new MailMessage();

        mailMessage.setSender(environment.getProperty("spring.mail.username"));
        mailMessage.setReceiver(user.getEmail());

        mailMessage.setSubject("New notification");

        final String messageContent = EmailTemplates.getNewNotificationTemplate()
                .replace("{NAME}", user.getName());

        mailMessage.setContent(messageContent);

        mailMessageRepository.save(mailMessage);

        return true;
    }

    public boolean sendMedicalHelpRequestMail(Refugee refugee) {

        MailMessage mailMessage = new MailMessage();

        mailMessage.setSender(environment.getProperty("spring.mail.username"));
        mailMessage.setReceiver(refugee.getFacility().getResponsibleUser().getEmail());

        mailMessage.setSubject("Medical help request");

        final String messageContent = EmailTemplates.getMedicalHelpRequestTemplate()
                .replace("{NAME}", refugee.getFacility().getResponsibleUser().getName())
                .replace("{REFUGEE_NAME}", refugee.getName())
                .replace("{SHELTER}", refugee.getFacility().getFacilityInformation());


        mailMessage.setContent(messageContent);

        if(!sendEmail(mailMessage))
            return false;

        return true;
    }

    public List<MailMessage> getPendingMails(long threadId) {

        return mailMessageRepository.getPendingMails(threadId);
    }

    public void deleteMessage(Long id) {

        mailMessageRepository.deleteById(id);
    }

    public void reserveMailMessages(long threadId) {

        mailMessageRepository.reserveMailMessages(threadId);
    }

    public void updateMailMessage(MailMessage message){

        mailMessageRepository.save(message);
    }

    public void deleteMailMessages(long threadId) {

        mailMessageRepository.deleteMailMessages(threadId);
    }


}
