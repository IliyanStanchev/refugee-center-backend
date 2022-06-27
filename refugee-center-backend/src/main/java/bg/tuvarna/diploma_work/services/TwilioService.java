package bg.tuvarna.diploma_work.services;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class TwilioService {

    @Autowired
    private Environment environment;

    @Autowired
    private LogService logService;

    public boolean sendMessage(String receiverPhoneNumber, String message) {

        final String ACCOUNT_SID = environment.getProperty("spring.twilio.account.sid");
        final String AUTH_TOKEN = environment.getProperty("spring.twilio.auth.token");
        final String TWILIO_NUMBER = environment.getProperty("spring.twilio.number");

        try {
            Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

            Message.creator(
                    new PhoneNumber(receiverPhoneNumber),
                    new PhoneNumber(TWILIO_NUMBER),
                    message).create();
        } catch (Exception e) {

            logService.logErrorMessage("TwilioService::sendMessage", "Error sending message to " + receiverPhoneNumber + ": " + e.getMessage());
            return false;
        }


        return true;
    }
}
