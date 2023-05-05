package uk.gov.companieshouse.authcodenotification.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AuthCodeNotificationControllerTest {

    @Test
    public void testSendEmailReturnsSuccess (){

        AuthCodeNotificationController controller = new AuthCodeNotificationController();
        ResponseEntity<Object> responseEntity = controller.sendEmail();
        assertEquals( 200, responseEntity.getStatusCode().value() );

    }
}
