package uk.gov.companieshouse.authcodenotification.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.api.model.update.OverseasEntityDataApi;
import uk.gov.companieshouse.authcodenotification.exception.EncryptionException;
import uk.gov.companieshouse.authcodenotification.exception.ServiceException;
import uk.gov.companieshouse.authcodenotification.utils.Encrypter;

import java.security.InvalidKeyException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthCodeNotificationServiceTest {

    private static final String REQUEST_ID = "abc";
    private static final String AUTH_CODE = "auth123";
    private static final String COMPANY_NUMBER = "OE123456";
    private static final String COMPANY_NAME = "Test Company";
    private static final String TEST_EMAIL = "test@oe.com";
    private static final String DUMMY_ENCRYPTED_AUTH_CODE = "yGJhg876JggfDkjjkh987689jgh";
    private static final String DUMMY_ENCRYPTION_KEY = "jhtefgOBoV+YIjhgrfEvae9Up476543j";
    private static final String ENCRYPTION_ERROR_MESSAGE = "Error when encrypting text";

    @InjectMocks
    private AuthCodeNotificationService authCodeNotificationService;

    @Mock
    private PrivateDataRetrievalService privateDataRetrievalService;

    @Mock
    private PublicDataRetrievalService publicDataRetrievalService;

    @Mock
    private EmailService emailService;

    @Mock
    private Encrypter encrypter;

    private OverseasEntityDataApi overseasEntityDataApi;

    private CompanyProfileApi companyProfileApi;

    @BeforeEach
    void setup() throws EncryptionException, InvalidKeyException {
        ReflectionTestUtils.setField(authCodeNotificationService, "aesKeyString", DUMMY_ENCRYPTION_KEY);
        overseasEntityDataApi = new OverseasEntityDataApi();
        companyProfileApi = new CompanyProfileApi();

        when(encrypter.encrypt(AUTH_CODE, DUMMY_ENCRYPTION_KEY)).thenReturn(DUMMY_ENCRYPTED_AUTH_CODE);
    }

    @Test
    void testSendAuthCodeEmailSuccessfulRetrieveAndSend() throws ServiceException, EncryptionException, InvalidKeyException {
        overseasEntityDataApi.setEmail(TEST_EMAIL);
        when(privateDataRetrievalService.getOverseasEntityData(REQUEST_ID, COMPANY_NUMBER)).thenReturn(overseasEntityDataApi);
        companyProfileApi.setCompanyName(COMPANY_NAME);
        when(publicDataRetrievalService.getCompanyProfile(REQUEST_ID, COMPANY_NUMBER)).thenReturn(companyProfileApi);

        authCodeNotificationService.sendAuthCodeEmail(REQUEST_ID, AUTH_CODE, COMPANY_NUMBER);

        verify(encrypter, times(1)).encrypt(AUTH_CODE, DUMMY_ENCRYPTION_KEY);
        verify(privateDataRetrievalService, times(1)).getOverseasEntityData(REQUEST_ID, COMPANY_NUMBER);
        verify(publicDataRetrievalService, times(1)).getCompanyProfile(REQUEST_ID, COMPANY_NUMBER);
        verify(emailService, times(1)).sendAuthCodeEmail(REQUEST_ID, DUMMY_ENCRYPTED_AUTH_CODE, COMPANY_NAME, COMPANY_NUMBER, TEST_EMAIL);
    }

    @Test
    void testSendAuthCodeEmailThrowsServiceExceptionWhenEmailIsNull() throws ServiceException {
        when(privateDataRetrievalService.getOverseasEntityData(REQUEST_ID, COMPANY_NUMBER)).thenReturn(overseasEntityDataApi);
        assertThrows(ServiceException.class, () -> authCodeNotificationService.sendAuthCodeEmail(REQUEST_ID, AUTH_CODE, COMPANY_NUMBER));
    }

    @Test
    void testSendAuthCodeEmailThrowsServiceExceptionWhenEmailIsEmpty() throws ServiceException {
        overseasEntityDataApi.setEmail("");
        when(privateDataRetrievalService.getOverseasEntityData(REQUEST_ID, COMPANY_NUMBER)).thenReturn(overseasEntityDataApi);
        assertThrows(ServiceException.class, () -> authCodeNotificationService.sendAuthCodeEmail(REQUEST_ID, AUTH_CODE, COMPANY_NUMBER));
    }

    @Test
    void testSendAuthCodeEmailThrowsServiceExceptionWhenEmailIsBlank() throws ServiceException {
        overseasEntityDataApi.setEmail("   ");
        when(privateDataRetrievalService.getOverseasEntityData(REQUEST_ID, COMPANY_NUMBER)).thenReturn(overseasEntityDataApi);
        assertThrows(ServiceException.class, () -> authCodeNotificationService.sendAuthCodeEmail(REQUEST_ID, AUTH_CODE, COMPANY_NUMBER));
    }

    @Test
    void testSendAuthCodeEmailThrowsServiceExceptionWhenCompanyNameIsNull() throws ServiceException {
        overseasEntityDataApi.setEmail(TEST_EMAIL);
        when(privateDataRetrievalService.getOverseasEntityData(REQUEST_ID, COMPANY_NUMBER)).thenReturn(overseasEntityDataApi);
        when(publicDataRetrievalService.getCompanyProfile(REQUEST_ID, COMPANY_NUMBER)).thenReturn(companyProfileApi);
        assertThrows(ServiceException.class, () -> authCodeNotificationService.sendAuthCodeEmail(REQUEST_ID, AUTH_CODE, COMPANY_NUMBER));
    }

    @Test
    void testSendAuthCodeEmailThrowsServiceExceptionWhenCompanyNameIsEmpty() throws ServiceException {
        overseasEntityDataApi.setEmail(TEST_EMAIL);
        when(privateDataRetrievalService.getOverseasEntityData(REQUEST_ID, COMPANY_NUMBER)).thenReturn(overseasEntityDataApi);
        companyProfileApi.setCompanyName("");
        when(publicDataRetrievalService.getCompanyProfile(REQUEST_ID, COMPANY_NUMBER)).thenReturn(companyProfileApi);
        assertThrows(ServiceException.class, () -> authCodeNotificationService.sendAuthCodeEmail(REQUEST_ID, AUTH_CODE, COMPANY_NUMBER));
    }

    @Test
    void testSendAuthCodeEmailThrowsServiceExceptionWhenCompanyNameIsBlank() throws ServiceException {
        overseasEntityDataApi.setEmail(TEST_EMAIL);
        when(privateDataRetrievalService.getOverseasEntityData(REQUEST_ID, COMPANY_NUMBER)).thenReturn(overseasEntityDataApi);
        companyProfileApi.setCompanyName("   ");
        when(publicDataRetrievalService.getCompanyProfile(REQUEST_ID, COMPANY_NUMBER)).thenReturn(companyProfileApi);
        assertThrows(ServiceException.class, () -> authCodeNotificationService.sendAuthCodeEmail(REQUEST_ID, AUTH_CODE, COMPANY_NUMBER));
    }

    @Test
    void testSendAuthCodeEmailThrowsServiceExceptionIfEncryptedAuthCodeIsNull() throws EncryptionException, InvalidKeyException {
        reset(encrypter);
        when(encrypter.encrypt(AUTH_CODE, DUMMY_ENCRYPTION_KEY)).thenReturn(null);
        assertThrows(ServiceException.class, () -> authCodeNotificationService.sendAuthCodeEmail(REQUEST_ID, AUTH_CODE, COMPANY_NUMBER));
    }

    @Test
    void testSendAuthCodeEmailThrowsServiceExceptionIfEncryptedAuthCodeIsBlank() throws EncryptionException, InvalidKeyException {
        reset(encrypter);
        when(encrypter.encrypt(AUTH_CODE, DUMMY_ENCRYPTION_KEY)).thenReturn("  ");
        assertThrows(ServiceException.class, () -> authCodeNotificationService.sendAuthCodeEmail(REQUEST_ID, AUTH_CODE, COMPANY_NUMBER));
    }

    @Test
    void testSendAuthCodeEmailThrowsServiceExceptionIfEncrypterThrowsEncryptionException() throws EncryptionException, InvalidKeyException {
        reset(encrypter);
        when(encrypter.encrypt(AUTH_CODE, DUMMY_ENCRYPTION_KEY)).thenThrow(new EncryptionException("something", new Exception()));
        Exception e = assertThrows(ServiceException.class, () -> authCodeNotificationService.sendAuthCodeEmail(REQUEST_ID, AUTH_CODE, COMPANY_NUMBER));
        assertEquals(ENCRYPTION_ERROR_MESSAGE, e.getMessage());
    }

    @Test
    void testSendAuthCodeEmailThrowsServiceExceptionIfEncrypterThrowsInvalidKeyException() throws EncryptionException, InvalidKeyException {
        reset(encrypter);
        when(encrypter.encrypt(AUTH_CODE, DUMMY_ENCRYPTION_KEY)).thenThrow(new InvalidKeyException("something"));
        Exception e = assertThrows(ServiceException.class, () -> authCodeNotificationService.sendAuthCodeEmail(REQUEST_ID, AUTH_CODE, COMPANY_NUMBER));
        assertEquals(ENCRYPTION_ERROR_MESSAGE, e.getMessage());
    }
}
