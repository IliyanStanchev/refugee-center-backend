package bg.tuvarna.diploma_work.helpers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class EmailTemplates {

    public static final String COMPANY_ADDRESS = "Varna 9000, Gotse Delchev 9";
    public static final String COMPANY_PHONE = "+359 08982231";
    public static final String COMPANY_EMAIL = "safe_shelter@sshelter.com";

    public static final String RESET_PASSWORD_TEMPLATE_PATH = "src/main/resources/templates/resetEmailTemplate.html";
    public static final String NEW_USER_TEMPLATE_PATH = "src/main/resources/templates/newUserEmailTemplate.html";
    public static final String DECLINED_REGISTRATION_TEMPLATE_PATH = "src/main/resources/templates/declinedRegistrationTemplate.html";

    private static String getTemplate(String htmlPath) {

        return convertHtmlToString(htmlPath)
                .replace("{ADDRESS}", COMPANY_ADDRESS)
                .replace("{PHONE}", COMPANY_PHONE)
                .replace("{EMAIL}", COMPANY_EMAIL);
    }

    private static String convertHtmlToString(String htmlPath){
        StringBuilder contentBuilder = new StringBuilder();
        try {
            BufferedReader in = new BufferedReader(new FileReader(htmlPath));
            String str;
            while ((str = in.readLine()) != null) {
                contentBuilder.append(str);
            }
            in.close();
        } catch (IOException e) {
            return "";
        }

        return contentBuilder.toString();
    }

    public static String getResetPasswordTemplate() {
        return getTemplate(RESET_PASSWORD_TEMPLATE_PATH);
    }

    public static String getNewUserTemplate() {
        return getTemplate(NEW_USER_TEMPLATE_PATH);
    }

    public static String getDeclinedRegistrationTemplate() {
        return getTemplate(DECLINED_REGISTRATION_TEMPLATE_PATH);
    }
}
