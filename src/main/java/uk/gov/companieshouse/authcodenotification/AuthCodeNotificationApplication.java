package uk.gov.companieshouse.authcodenotification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AuthCodeNotificationApplication {

    public static final String APP_NAMESPACE = "auth-code-notification";
    public static void main( String[] args ) {
        SpringApplication.run(AuthCodeNotificationApplication.class);
    }
}
