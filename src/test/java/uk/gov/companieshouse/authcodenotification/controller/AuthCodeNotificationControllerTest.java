package uk.gov.companieshouse.authcodenotification.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.authcodenotification.exception.ServiceException;
import uk.gov.companieshouse.authcodenotification.service.AuthCodeNotificationService;
import uk.gov.companieshouse.authcodenotification.utils.DataSanitisation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthCodeNotificationControllerTest {

    private static final String REQUEST_ID = "abc";

    private static final String COMPANY_NUMBER = "OE123456";

    @InjectMocks
    private AuthCodeNotificationController controller;

    @Mock
    private AuthCodeNotificationService authCodeNotificationService;

    @Mock
    private DataSanitisation dataSanitisation;

    @Test
    void testSendEmailReturnsSuccess() {
        ResponseEntity<Object> responseEntity = controller.sendEmail(REQUEST_ID, COMPANY_NUMBER);
        assertEquals( 200, responseEntity.getStatusCode().value() );
    }

    @Test
    void testSendEmailReturnsInternalServerErrorWhenServiceCallFails() throws ServiceException{
        when(dataSanitisation.makeStringSafeForLogging(COMPANY_NUMBER)).thenReturn(COMPANY_NUMBER);
        doThrow(new ServiceException("")).when(authCodeNotificationService).sendAuthCodeEmail(REQUEST_ID , COMPANY_NUMBER);
        ResponseEntity<Object> responseEntity = controller.sendEmail(REQUEST_ID, COMPANY_NUMBER);
        assertEquals( 500, responseEntity.getStatusCode().value() );
    }
}
