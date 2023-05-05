package uk.gov.companieshouse.authcodenotification.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AuthCodeNotificationControllerTest {

    @Test
    void testSendEmailReturnsSuccess (){

        AuthCodeNotificationController controller = new AuthCodeNotificationController();
        ResponseEntity<Object> responseEntity = controller.sendEmail("abc","012345657");
        assertEquals( 200, responseEntity.getStatusCode().value() );
    }
}
