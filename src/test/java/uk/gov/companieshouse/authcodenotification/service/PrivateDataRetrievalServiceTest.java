package uk.gov.companieshouse.authcodenotification.service;

import com.google.api.client.http.HttpResponseException;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PrivateDataRetrievalServiceTest {

    private static final String REQUEST_ID = "abc";

    private static final String COMPANY_NUMBER = "OE123456";

    private static final String GET_OVERSEAS_ENTITY_DATA_URL =
            String.format("/overseas-entity/%s/entity-data", COMPANY_NUMBER);

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

    @Mock
    private HttpResponseException.Builder builder;

    @BeforeEach
    void setup() {
        when(apiClientService.getInternalApiClient()).thenReturn(internalApiClient);
        when(internalApiClient.privateOverseasEntityDataHandler()).thenReturn(privateOverseasEntityDataHandler);
        when(privateOverseasEntityDataHandler.getOverseasEntityData(GET_OVERSEAS_ENTITY_DATA_URL))
                .thenReturn(privateOverseasEntityDataGet);
    }

    @Test
    void testGetOverseasEntityDataWhenSuccessful() throws ApiErrorResponseException, URIValidationException, ServiceException {
        OverseasEntityDataApi overseasEntityDataApi = new OverseasEntityDataApi();
        when(privateOverseasEntityDataGet.execute()).thenReturn(overseasEntityDataApiResponse);
        when(overseasEntityDataApiResponse.getData()).thenReturn(overseasEntityDataApi);

        OverseasEntityDataApi returnedOverseasEntityDataApi = privateDataRetrievalService.getOverseasEntityData(REQUEST_ID, COMPANY_NUMBER);

        assertEquals(overseasEntityDataApi, returnedOverseasEntityDataApi);
        verify(apiClientService, times(1)).getInternalApiClient();
    }

    @Test
    void testGetOverseasEntityDataWhenURIValidationExceptionExceptionIsThrown() throws ApiErrorResponseException, URIValidationException {
        when(privateOverseasEntityDataGet.execute()).thenThrow(new URIValidationException(""));
        assertThrows(ServiceException.class, () -> privateDataRetrievalService.getOverseasEntityData(REQUEST_ID, COMPANY_NUMBER));
    }

    @Test
    void testGetOverseasEntityDataWhenApiErrorResponseExceptionIsThrown() throws ApiErrorResponseException, URIValidationException {
        when(privateOverseasEntityDataGet.execute()).thenThrow(new ApiErrorResponseException(builder));
        assertThrows(ServiceException.class, () -> privateDataRetrievalService.getOverseasEntityData(REQUEST_ID, COMPANY_NUMBER));
    }

}
