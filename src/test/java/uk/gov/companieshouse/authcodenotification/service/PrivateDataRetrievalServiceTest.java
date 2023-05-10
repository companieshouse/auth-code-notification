package uk.gov.companieshouse.authcodenotification.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.handler.update.PrivateOverseasEntityDataHandler;
import uk.gov.companieshouse.api.handler.update.request.PrivateOverseasEntityDataGet;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.api.model.update.OverseasEntityDataApi;
import uk.gov.companieshouse.authcodenotification.client.ApiClientService;
import uk.gov.companieshouse.authcodenotification.exception.ServiceException;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PrivateDataRetrievalServiceTest {

    private static final String REQUEST_ID = "abc";

    private static final String COMPANY_NUMBER = "OE123456";

    @InjectMocks
    private PrivateDataRetrievalService privateDataRetrievalService;

    @Mock
    private ApiClientService apiClientService;

    @Mock
    private InternalApiClient internalApiClient;

    @Mock
    private PrivateOverseasEntityDataHandler privateOverseasEntityDataHandler;

    @Mock
    private PrivateOverseasEntityDataGet privateOverseasEntityDataGet;

    @Mock
    private ApiResponse<OverseasEntityDataApi> overseasEntityDataApiResponse;

    @BeforeEach
    public void init() throws IOException {
       when(apiClientService.getInternalApiClient()).thenReturn(internalApiClient);
       when(internalApiClient.privateOverseasEntityDataHandler()).thenReturn(privateOverseasEntityDataHandler);
       when(privateOverseasEntityDataHandler.getOverseasEntityData(anyString())).thenReturn(privateOverseasEntityDataGet);
    }

    @Test
    void testGetOverseasEntityDataWhenSuccessful() throws ApiErrorResponseException, URIValidationException, ServiceException {
        OverseasEntityDataApi overseasEntityDataApi = new OverseasEntityDataApi();
        when(privateOverseasEntityDataGet.execute()).thenReturn(overseasEntityDataApiResponse);
        when(overseasEntityDataApiResponse.getData()).thenReturn(overseasEntityDataApi);
        privateDataRetrievalService.getOverseasEntityData(REQUEST_ID, COMPANY_NUMBER);
        verify(apiClientService, times(1)).getInternalApiClient();
    }

}
