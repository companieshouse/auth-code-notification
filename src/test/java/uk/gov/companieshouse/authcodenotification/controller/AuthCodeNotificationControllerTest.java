package uk.gov.companieshouse.authcodenotification.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.authcodenotification.exception.ServiceException;
import uk.gov.companieshouse.authcodenotification.model.SendEmailRequestDto;
import uk.gov.companieshouse.authcodenotification.service.AuthCodeNotificationService;
import uk.gov.companieshouse.authcodenotification.utils.DataSanitiser;
import uk.gov.companieshouse.authcodenotification.validation.AuthCodeEmailValidator;
import uk.gov.companieshouse.service.rest.err.Errors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthCodeNotificationControllerTest {

    private static final String REQUEST_ID = "abc";
    private static final String AUTH_CODE = "auth123";

    private static final String COMPANY_NUMBER = "OE123456";

    @InjectMocks
    private AuthCodeNotificationController controller;

    @Mock
    private AuthCodeNotificationService authCodeNotificationService;

    @Mock
    private AuthCodeEmailValidator authCodeEmailValidator;

    @Mock
    private DataSanitiser dataSanitiser;

    private  SendEmailRequestDto sendEmailRequestDto;

    @BeforeEach
    void setup() {
        sendEmailRequestDto = new SendEmailRequestDto();
        sendEmailRequestDto.setAuthCode(AUTH_CODE);
        when(dataSanitiser.makeStringSafe(COMPANY_NUMBER)).thenReturn(COMPANY_NUMBER);
        when(dataSanitiser.makeStringSafe(AUTH_CODE)).thenReturn(AUTH_CODE);
        Errors errors = new Errors();
        when(authCodeEmailValidator.validate(eq(COMPANY_NUMBER), eq(AUTH_CODE), any(), eq(REQUEST_ID))).thenReturn(errors);
    }

    @Test
    void testSendEmailReturnsSuccess() {
        ResponseEntity<Object> responseEntity = controller.sendEmail(REQUEST_ID, sendEmailRequestDto, COMPANY_NUMBER);
        assertEquals( 200, responseEntity.getStatusCode().value() );
    }

    @Test
    void testSendEmailReturnsInternalServerErrorWhenServiceCallFails() throws ServiceException {
        doThrow(new ServiceException("")).when(authCodeNotificationService).sendAuthCodeEmail(REQUEST_ID, AUTH_CODE, COMPANY_NUMBER);
        ResponseEntity<Object> responseEntity = controller.sendEmail(REQUEST_ID, sendEmailRequestDto, COMPANY_NUMBER);
        assertEquals( 500, responseEntity.getStatusCode().value() );
    }
}
