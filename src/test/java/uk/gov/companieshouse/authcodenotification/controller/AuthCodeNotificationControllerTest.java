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
import uk.gov.companieshouse.authcodenotification.utils.Encrypter;
import uk.gov.companieshouse.authcodenotification.validation.AuthCodeEmailValidator;
import uk.gov.companieshouse.service.rest.err.Err;
import uk.gov.companieshouse.service.rest.err.Errors;
import uk.gov.companieshouse.service.rest.response.ChResponseBody;

import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthCodeNotificationControllerTest {

    private static final String REQUEST_ID = "abc";
    private static final String AUTH_CODE = "auth123";
    private static final String COMPANY_NUMBER = "OE123456";
    private static final String ENCRYPTED_AUTH_CODE = "yGJhg876JggfDkjjkh987689jgh";

    @InjectMocks
    private AuthCodeNotificationController controller;

    @Mock
    private AuthCodeNotificationService authCodeNotificationService;

    @Mock
    private AuthCodeEmailValidator authCodeEmailValidator;

    @Mock
    private DataSanitiser dataSanitiser;

    @Mock
    private Encrypter encrypter;

    private  SendEmailRequestDto sendEmailRequestDto;

    @BeforeEach
    void setup() {
        sendEmailRequestDto = new SendEmailRequestDto();
        sendEmailRequestDto.setAuthCode(AUTH_CODE);
        when(dataSanitiser.makeStringSafe(COMPANY_NUMBER)).thenReturn(COMPANY_NUMBER);
        when(dataSanitiser.makeStringSafe(AUTH_CODE)).thenReturn(AUTH_CODE);
    }

    @Test
    void testSendEmailReturnsSuccess() throws Exception {
        Errors errors = new Errors();
        when(authCodeEmailValidator.validate(eq(COMPANY_NUMBER), eq(AUTH_CODE), any(), eq(REQUEST_ID))).thenReturn(errors);
        when(encrypter.encrypt(AUTH_CODE)).thenReturn(ENCRYPTED_AUTH_CODE);
        ResponseEntity<Object> responseEntity = controller.sendEmail(REQUEST_ID, sendEmailRequestDto, COMPANY_NUMBER);
        assertEquals( 200, responseEntity.getStatusCode().value() );
    }

    @Test
    void testSendEmailEncryptsAuthCode() throws Exception {
        Errors errors = new Errors();
        when(authCodeEmailValidator.validate(eq(COMPANY_NUMBER), eq(AUTH_CODE), any(), eq(REQUEST_ID))).thenReturn(errors);
        when(encrypter.encrypt(AUTH_CODE)).thenReturn(ENCRYPTED_AUTH_CODE);
        controller.sendEmail(REQUEST_ID, sendEmailRequestDto, COMPANY_NUMBER);
        verify(authCodeNotificationService, times(1)).sendAuthCodeEmail(REQUEST_ID, ENCRYPTED_AUTH_CODE, COMPANY_NUMBER);
    }

    @Test
    void testSendEmailReturnsInternalServerErrorIfEncryptedAuthCodeIsNull() throws Exception {
        Errors errors = new Errors();
        when(authCodeEmailValidator.validate(eq(COMPANY_NUMBER), eq(AUTH_CODE), any(), eq(REQUEST_ID))).thenReturn(errors);
        when(encrypter.encrypt(AUTH_CODE)).thenReturn(null);
        ResponseEntity<Object> responseEntity = controller.sendEmail(REQUEST_ID, sendEmailRequestDto, COMPANY_NUMBER);
        assertEquals(500, responseEntity.getStatusCode().value());
    }

    @Test
    void testSendEmailReturnsInternalServerErrorIfEncryptedAuthCodeIsBlank() throws Exception {
        Errors errors = new Errors();
        when(authCodeEmailValidator.validate(eq(COMPANY_NUMBER), eq(AUTH_CODE), any(), eq(REQUEST_ID))).thenReturn(errors);
        when(encrypter.encrypt(AUTH_CODE)).thenReturn("   ");
        ResponseEntity<Object> responseEntity = controller.sendEmail(REQUEST_ID, sendEmailRequestDto, COMPANY_NUMBER);
        assertEquals(500, responseEntity.getStatusCode().value());
    }

    @Test
    void testSendEmailReturnsInternalServerErrorIfEncryptAuthCodeThrowsException() throws Exception {
        Errors errors = new Errors();
        when(authCodeEmailValidator.validate(eq(COMPANY_NUMBER), eq(AUTH_CODE), any(), eq(REQUEST_ID))).thenReturn(errors);
        when(encrypter.encrypt(AUTH_CODE)).thenThrow(new NoSuchAlgorithmException());
        ResponseEntity<Object> responseEntity = controller.sendEmail(REQUEST_ID, sendEmailRequestDto, COMPANY_NUMBER);
        assertEquals(500, responseEntity.getStatusCode().value());
    }

    @Test
    void testSendEmailReturnsInternalServerErrorWhenServiceCallFails() throws Exception {
        Errors errors = new Errors();
        when(authCodeEmailValidator.validate(eq(COMPANY_NUMBER), eq(AUTH_CODE), any(), eq(REQUEST_ID))).thenReturn(errors);
        when(encrypter.encrypt(AUTH_CODE)).thenReturn(ENCRYPTED_AUTH_CODE);
        doThrow(new ServiceException("")).when(authCodeNotificationService).sendAuthCodeEmail(REQUEST_ID, ENCRYPTED_AUTH_CODE, COMPANY_NUMBER);
        ResponseEntity<Object> responseEntity = controller.sendEmail(REQUEST_ID, sendEmailRequestDto, COMPANY_NUMBER);
        assertEquals( 500, responseEntity.getStatusCode().value() );
    }

    @Test
    void testSendValidationFailureReturnsBadRequest() {
        Errors errors = new Errors();
        final String errorLocation = "EXAMPLE_ERROR_LOCATION";
        final String error = "EXAMPLE_ERROR";
        Err err = Err.invalidBodyBuilderWithLocation(errorLocation).withError(error).build();
        errors.addError(err);
        when(authCodeEmailValidator.validate(eq(COMPANY_NUMBER), eq(AUTH_CODE), any(), eq(REQUEST_ID))).thenReturn(errors);
        ResponseEntity<Object> responseEntity = controller.sendEmail(REQUEST_ID, sendEmailRequestDto, COMPANY_NUMBER);
        assertEquals( 400, responseEntity.getStatusCode().value() );
        assertTrue( ((ChResponseBody<?>) Objects.requireNonNull(responseEntity.getBody())).isErrorBody() );
        assertEquals( errors, ((ChResponseBody<?>)responseEntity.getBody()).getErrorBody() );
    }
}
