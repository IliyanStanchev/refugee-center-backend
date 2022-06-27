package bg.tuvarna.diploma_work;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class RefugeeCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(RefugeeCenterApplication.class, args);
    }
}
