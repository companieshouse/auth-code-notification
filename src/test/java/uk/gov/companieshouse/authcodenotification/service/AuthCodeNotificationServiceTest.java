package uk.gov.companieshouse.authcodenotification.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.api.model.update.OverseasEntityDataApi;
import uk.gov.companieshouse.authcodenotification.exception.ServiceException;
import uk.gov.companieshouse.authcodenotification.utils.Encrypter;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
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
    void setup() throws ServiceException {
        overseasEntityDataApi = new OverseasEntityDataApi();
        companyProfileApi = new CompanyProfileApi();
        when(encrypter.encrypt(eq(REQUEST_ID), eq(AUTH_CODE), anyMap())).thenReturn(DUMMY_ENCRYPTED_AUTH_CODE);
    }

    @Test
    void testSendAuthCodeEmailSuccessfulRetrieveAndSend() throws ServiceException {
        overseasEntityDataApi.setEmail(TEST_EMAIL);
        when(privateDataRetrievalService.getOverseasEntityData(REQUEST_ID, COMPANY_NUMBER)).thenReturn(overseasEntityDataApi);
        companyProfileApi.setCompanyName(COMPANY_NAME);
        when(publicDataRetrievalService.getCompanyProfile(REQUEST_ID, COMPANY_NUMBER)).thenReturn(companyProfileApi);

        authCodeNotificationService.sendAuthCodeEmail(REQUEST_ID, AUTH_CODE, COMPANY_NUMBER);

        verify(encrypter, times(1)).encrypt(eq(REQUEST_ID), eq(AUTH_CODE), anyMap());
        verify(privateDataRetrievalService, times(1)).getOverseasEntityData(REQUEST_ID, COMPANY_NUMBER);
        verify(publicDataRetrievalService, times(1)).getCompanyProfile(REQUEST_ID, COMPANY_NUMBER);
        verify(emailService, times(1)).sendAuthCodeEmail(REQUEST_ID, DUMMY_ENCRYPTED_AUTH_CODE, COMPANY_NAME, COMPANY_NUMBER, TEST_EMAIL);
    }

    @Test
    void testSendAuthCodeEmailExceptionThrownWhenEmailIsNull() throws ServiceException {
        when(privateDataRetrievalService.getOverseasEntityData(REQUEST_ID, COMPANY_NUMBER)).thenReturn(overseasEntityDataApi);
        assertThrows(ServiceException.class, () -> authCodeNotificationService.sendAuthCodeEmail(REQUEST_ID, AUTH_CODE, COMPANY_NUMBER));
    }

    @Test
    void testSendAuthCodeEmailExceptionThrownWhenEmailIsEmpty() throws ServiceException {
        overseasEntityDataApi.setEmail("");
        when(privateDataRetrievalService.getOverseasEntityData(REQUEST_ID, COMPANY_NUMBER)).thenReturn(overseasEntityDataApi);
        assertThrows(ServiceException.class, () -> authCodeNotificationService.sendAuthCodeEmail(REQUEST_ID, AUTH_CODE, COMPANY_NUMBER));
    }

    @Test
    void testSendAuthCodeEmailExceptionThrownWhenEmailIsBlank() throws ServiceException {
        overseasEntityDataApi.setEmail("   ");
        when(privateDataRetrievalService.getOverseasEntityData(REQUEST_ID, COMPANY_NUMBER)).thenReturn(overseasEntityDataApi);
        assertThrows(ServiceException.class, () -> authCodeNotificationService.sendAuthCodeEmail(REQUEST_ID, AUTH_CODE, COMPANY_NUMBER));
    }

    @Test
    void testSendAuthCodeEmailExceptionThrownWhenCompanyNameIsNull() throws ServiceException {
        overseasEntityDataApi.setEmail(TEST_EMAIL);
        when(privateDataRetrievalService.getOverseasEntityData(REQUEST_ID, COMPANY_NUMBER)).thenReturn(overseasEntityDataApi);
        when(publicDataRetrievalService.getCompanyProfile(REQUEST_ID, COMPANY_NUMBER)).thenReturn(companyProfileApi);
        assertThrows(ServiceException.class, () -> authCodeNotificationService.sendAuthCodeEmail(REQUEST_ID, AUTH_CODE, COMPANY_NUMBER));
    }

    @Test
    void testSendAuthCodeEmailExceptionThrownWhenCompanyNameIsEmpty() throws ServiceException {
        overseasEntityDataApi.setEmail(TEST_EMAIL);
        when(privateDataRetrievalService.getOverseasEntityData(REQUEST_ID, COMPANY_NUMBER)).thenReturn(overseasEntityDataApi);
        companyProfileApi.setCompanyName("");
        when(publicDataRetrievalService.getCompanyProfile(REQUEST_ID, COMPANY_NUMBER)).thenReturn(companyProfileApi);
        assertThrows(ServiceException.class, () -> authCodeNotificationService.sendAuthCodeEmail(REQUEST_ID, AUTH_CODE, COMPANY_NUMBER));
    }

    @Test
    void testSendAuthCodeEmailExceptionThrownWhenCompanyNameIsBlank() throws ServiceException {
        overseasEntityDataApi.setEmail(TEST_EMAIL);
        when(privateDataRetrievalService.getOverseasEntityData(REQUEST_ID, COMPANY_NUMBER)).thenReturn(overseasEntityDataApi);
        companyProfileApi.setCompanyName("   ");
        when(publicDataRetrievalService.getCompanyProfile(REQUEST_ID, COMPANY_NUMBER)).thenReturn(companyProfileApi);
        assertThrows(ServiceException.class, () -> authCodeNotificationService.sendAuthCodeEmail(REQUEST_ID, AUTH_CODE, COMPANY_NUMBER));
    }

    @Test
    void testSendAuthCodeEmailThrowsExceptionIfEncryptedAuthCodeIsNull() throws ServiceException {
        reset(encrypter);
        when(encrypter.encrypt(eq(REQUEST_ID), eq(AUTH_CODE), anyMap())).thenReturn(null);
        assertThrows(ServiceException.class, () -> authCodeNotificationService.sendAuthCodeEmail(REQUEST_ID, AUTH_CODE, COMPANY_NUMBER));
    }

    @Test
    void testSendAuthCodeEmailThrowsExceptionIfEncryptedAuthCodeIsBlank() throws ServiceException {
        reset(encrypter);
        when(encrypter.encrypt(eq(REQUEST_ID), eq(AUTH_CODE), anyMap())).thenReturn("  ");
        assertThrows(ServiceException.class, () -> authCodeNotificationService.sendAuthCodeEmail(REQUEST_ID, AUTH_CODE, COMPANY_NUMBER));
    }
}
