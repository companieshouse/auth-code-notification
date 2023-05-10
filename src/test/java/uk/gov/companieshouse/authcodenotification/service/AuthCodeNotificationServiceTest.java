package uk.gov.companieshouse.authcodenotification.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.model.update.OverseasEntityDataApi;
import uk.gov.companieshouse.authcodenotification.exception.ServiceException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthCodeNotificationServiceTest {

    private static final String REQUEST_ID = "abc";

    private static final String COMPANY_NUMBER = "OE123456";

    private static final String TEST_EMAIL = "test@oe.com";

    @InjectMocks
    private AuthCodeNotificationService authCodeNotificationService;

    @Mock
    private PrivateDataRetrievalService privateDataRetrievalService;

    @Test
    void testSuccessfulRetrieveAndSend() throws ServiceException {
        OverseasEntityDataApi overseasEntityDataApi = new OverseasEntityDataApi();
        overseasEntityDataApi.setEmail(TEST_EMAIL);
        when(privateDataRetrievalService.getOverseasEntityData(REQUEST_ID, COMPANY_NUMBER)).thenReturn(overseasEntityDataApi);
        authCodeNotificationService.sendAuthCodeEmail(REQUEST_ID, COMPANY_NUMBER);
        verify(privateDataRetrievalService, times(1)).getOverseasEntityData(REQUEST_ID, COMPANY_NUMBER);
    }

    @Test
    void testExceptionThrownWhenEmailIsNull() throws ServiceException {
        OverseasEntityDataApi overseasEntityDataApi = new OverseasEntityDataApi();
        when(privateDataRetrievalService.getOverseasEntityData(REQUEST_ID, COMPANY_NUMBER)).thenReturn(overseasEntityDataApi);
        assertThrows(ServiceException.class, () -> {
            authCodeNotificationService.sendAuthCodeEmail(REQUEST_ID, COMPANY_NUMBER);
        });
    }

    @Test
    void testExceptionThrownWhenEmailIsEmpty() throws ServiceException {
        OverseasEntityDataApi overseasEntityDataApi = new OverseasEntityDataApi();
        overseasEntityDataApi.setEmail("");
        when(privateDataRetrievalService.getOverseasEntityData(REQUEST_ID, COMPANY_NUMBER)).thenReturn(overseasEntityDataApi);
        assertThrows(ServiceException.class, () -> {
            authCodeNotificationService.sendAuthCodeEmail(REQUEST_ID, COMPANY_NUMBER);
        });
    }

    @Test
    void testExceptionThrownWhenEmailIsBlank() throws ServiceException {
        OverseasEntityDataApi overseasEntityDataApi = new OverseasEntityDataApi();
        overseasEntityDataApi.setEmail("   ");
        when(privateDataRetrievalService.getOverseasEntityData(REQUEST_ID, COMPANY_NUMBER)).thenReturn(overseasEntityDataApi);
        assertThrows(ServiceException.class, () -> {
            authCodeNotificationService.sendAuthCodeEmail(REQUEST_ID, COMPANY_NUMBER);
        });
    }
}
