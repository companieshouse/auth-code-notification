package uk.gov.companieshouse.authcodenotification.service;

import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpResponseException;
import java.util.function.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.company.PrivateCompanyResourceHandler;
import uk.gov.companieshouse.api.handler.company.request.PrivateCompanyEmailGet;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.api.model.company.RegisteredEmailAddressJson;
import uk.gov.companieshouse.authcodenotification.config.ApiClientConfig;
import uk.gov.companieshouse.authcodenotification.exception.EntityNotFoundException;
import uk.gov.companieshouse.authcodenotification.exception.ServiceException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PrivateDataRetrievalServiceTest {

    private static final String REQUEST_ID = "abc";
    private static final String COMPANY_NUMBER = "OE123456";
    private static final String ORACLE_QUERY_API_URL = "http://oracle-query-api-test:8080";

    private static final String GET_OVERSEAS_ENTITY_DATA_URL =
            String.format("/company/%s/registered-email-address", COMPANY_NUMBER);

    @Mock
    private ApiClientConfig apiClientConfig;

    @Mock
    private Supplier<InternalApiClient> internalApiClientSupplier;

    @Mock
    private InternalApiClient internalApiClient;

    @Mock
    private PrivateCompanyResourceHandler privateCompanyResourceHandler;

    @Mock
    private PrivateCompanyEmailGet privateCompanyEmailGet;

    @Mock
    private ApiResponse<RegisteredEmailAddressJson> registeredEmailAddressJsonResponse;

    @Mock
    private HttpResponseException.Builder builder;

    @InjectMocks
    private PrivateDataRetrievalService underTest;

    @BeforeEach
    void setup() {
//        when(apiClientConfig.getInternalApiClient(anyString())).thenReturn(internalApiClientSupplier);
        when(internalApiClientSupplier.get()).thenReturn(internalApiClient);
    }

    @Test
    void testGetOverseasEntityDataWhenSuccessful() throws ApiErrorResponseException, URIValidationException, ServiceException {
        when(internalApiClient.privateCompanyResourceHandler()).thenReturn(privateCompanyResourceHandler);
        when(privateCompanyResourceHandler.getCompanyRegisteredEmailAddress(GET_OVERSEAS_ENTITY_DATA_URL)).thenReturn(privateCompanyEmailGet);

        RegisteredEmailAddressJson registeredEmailAddressJson = new RegisteredEmailAddressJson();
        when(privateCompanyEmailGet.execute()).thenReturn(registeredEmailAddressJsonResponse);
        when(registeredEmailAddressJsonResponse.getData()).thenReturn(registeredEmailAddressJson);

        RegisteredEmailAddressJson returnedRegisteredEmailAddressJson = underTest.getCompanyRegisteredEmailAddress(REQUEST_ID, COMPANY_NUMBER);

        assertEquals(registeredEmailAddressJson, returnedRegisteredEmailAddressJson);
        verify(internalApiClientSupplier, times(1)).get();
    }

    @Test
    void testGetOverseasEntityDataWhenURIValidationExceptionExceptionIsThrown() throws ApiErrorResponseException, URIValidationException {
        when(internalApiClient.privateCompanyResourceHandler()).thenReturn(privateCompanyResourceHandler);
        when(privateCompanyResourceHandler.getCompanyRegisteredEmailAddress(GET_OVERSEAS_ENTITY_DATA_URL)).thenReturn(privateCompanyEmailGet);
        when(privateCompanyEmailGet.execute()).thenThrow(new URIValidationException(""));

        assertThrows(ServiceException.class, () -> underTest.getCompanyRegisteredEmailAddress(REQUEST_ID, COMPANY_NUMBER));
    }

    @Test
    void testGetOverseasEntityDataWhenApiErrorResponseExceptionIsThrown() throws ApiErrorResponseException, URIValidationException {
        when(internalApiClient.privateCompanyResourceHandler()).thenReturn(privateCompanyResourceHandler);
        when(privateCompanyResourceHandler.getCompanyRegisteredEmailAddress(GET_OVERSEAS_ENTITY_DATA_URL)).thenReturn(privateCompanyEmailGet);
        when(privateCompanyEmailGet.execute()).thenThrow(new ApiErrorResponseException(builder));

        assertThrows(ServiceException.class, () -> underTest.getCompanyRegisteredEmailAddress(REQUEST_ID, COMPANY_NUMBER));
    }

    @Test
    void testGetCompanyProfileDataWhenHttpResponseExceptionWithNotFoundStatusIsThrown() throws IOException, URIValidationException {
        when(internalApiClient.privateCompanyResourceHandler()).thenReturn(privateCompanyResourceHandler);
        when(privateCompanyResourceHandler.getCompanyRegisteredEmailAddress(GET_OVERSEAS_ENTITY_DATA_URL)).thenReturn(privateCompanyEmailGet);

        HttpResponseException.Builder responseBuilder = new HttpResponseException.Builder(404, "", new HttpHeaders());
        when(privateCompanyEmailGet.execute()).thenThrow(new ApiErrorResponseException(responseBuilder));

        assertThrows(EntityNotFoundException.class, () -> underTest.getCompanyRegisteredEmailAddress(REQUEST_ID, COMPANY_NUMBER));
    }

    @Test
    void testGetCompanyProfileDataWhenHttpResponseExceptionWithoutNotFoundStatusIsThrown() throws IOException, URIValidationException {
        when(internalApiClient.privateCompanyResourceHandler()).thenReturn(privateCompanyResourceHandler);
        when(privateCompanyResourceHandler.getCompanyRegisteredEmailAddress(GET_OVERSEAS_ENTITY_DATA_URL)).thenReturn(privateCompanyEmailGet);

        HttpResponseException.Builder responseBuilder = new HttpResponseException.Builder(503, "", new HttpHeaders());
        when(privateCompanyEmailGet.execute()).thenThrow(new ApiErrorResponseException(responseBuilder));

        assertThrows(ServiceException.class, () -> underTest.getCompanyRegisteredEmailAddress(REQUEST_ID, COMPANY_NUMBER));
    }

}
