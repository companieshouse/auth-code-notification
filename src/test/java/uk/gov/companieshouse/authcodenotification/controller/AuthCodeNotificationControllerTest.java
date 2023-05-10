package uk.gov.companieshouse.authcodenotification.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.authcodenotification.exception.ServiceException;
import uk.gov.companieshouse.authcodenotification.service.AuthCodeNotificationService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class AuthCodeNotificationControllerTest {

    private static final String REQUEST_ID = "abc";

    private static final String COMPANY_NUMBER = "OE123456";

    @InjectMocks
    private AuthCodeNotificationController controller;

    @Mock
    private AuthCodeNotificationService authCodeNotificationService;

    @Test
    void testSendEmailReturnsSuccess() {
        ResponseEntity<Object> responseEntity = controller.sendEmail(REQUEST_ID ,COMPANY_NUMBER);
        assertEquals( 200, responseEntity.getStatusCode().value() );
    }

    @Test
    void testSendEmailReturnsInternalServerErrorWhenServiceCallFails() throws ServiceException{
        doThrow(new ServiceException("")).when(authCodeNotificationService).sendAuthCodeEmail(REQUEST_ID , COMPANY_NUMBER);
        ResponseEntity<Object> responseEntity = controller.sendEmail(REQUEST_ID ,COMPANY_NUMBER);
        assertEquals( 500, responseEntity.getStatusCode().value() );
    }
}
