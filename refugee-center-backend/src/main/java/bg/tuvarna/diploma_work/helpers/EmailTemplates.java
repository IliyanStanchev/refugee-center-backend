package bg.tuvarna.diploma_work.helpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class EmailTemplates {

    public static final String companyAddress   = "Varna 9000, Gotse Delchev 9";
    public static final String companyPhone     = "+359 08982231";
    public static final String companyEmail     = "safe_shelter@sshelter.com";

    public static final String resetPasswordTemplatePath = "src/main/resources/templates/resetEmailTemplate.html";

    public static final String getResetPasswordTemplate() {

        return getTemplate(resetPasswordTemplatePath);
    }

    private static String getTemplate(String htmlPath) {

        return convertHtmlToString(htmlPath)
                .replace("{ADDRESS}", companyAddress)
                .replace("{PHONE}"  , companyPhone)
                .replace("{EMAIL}"  , companyEmail);
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
}
