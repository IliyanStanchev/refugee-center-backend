package bg.tuvarna.diploma_work.services;

import bg.tuvarna.diploma_work.helpers.EmailTemplates;
import bg.tuvarna.diploma_work.models.Message;
import bg.tuvarna.diploma_work.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Service
public class MailService {

    @Autowired
    private Environment environment;

    @Autowired
    JavaMailSender javaMailSender;

    public boolean sendEmail(Message message) {
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
            LogService.logErrorMessage("sendEmail", message.toString());
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean sendResetPasswordEmail(User user, String newPassword) {

        Message message = new Message();

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

        Message message = new Message();

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

}
