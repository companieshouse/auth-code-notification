package uk.gov.companieshouse.authcodenotification;

import static org.springframework.boot.SpringApplication.run;

import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AuthCodeNotificationApplication {

    public static final String APPLICATION_NAME_SPACE = "auth-code-notification";

    public static void main(String[] args ) {
        run(AuthCodeNotificationApplication.class);
    }
}
