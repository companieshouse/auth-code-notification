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
import uk.gov.companieshouse.api.handler.company.CompanyResourceHandler;
import uk.gov.companieshouse.api.handler.company.request.CompanyGet;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.handler.update.PrivateOverseasEntityDataHandler;
import uk.gov.companieshouse.api.handler.update.request.PrivateOverseasEntityDataGet;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.api.model.update.OverseasEntityDataApi;
import uk.gov.companieshouse.authcodenotification.client.ApiClientService;
import uk.gov.companieshouse.authcodenotification.exception.ServiceException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PrivateDataRetrievalServiceTest {

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
    private CompanyResourceHandler companyResourceHandler;

    @Mock
    private CompanyGet companyGet;

    @Mock
    private ApiResponse<OverseasEntityDataApi> overseasEntityDataApiResponse;

    @Mock
    private ApiResponse<CompanyProfileApi> companyProfileApiResponse;

    @Mock
    private HttpResponseException.Builder builder;

    @BeforeEach
    void init() {
       when(apiClientService.getInternalApiClient()).thenReturn(internalApiClient);
    }

    @Test
    void testGetOverseasEntityDataWhenSuccessful() throws ApiErrorResponseException, URIValidationException, ServiceException {
        OverseasEntityDataApi overseasEntityDataApi = new OverseasEntityDataApi();
        stubEmailClient();
        when(privateOverseasEntityDataGet.execute()).thenReturn(overseasEntityDataApiResponse);
        when(overseasEntityDataApiResponse.getData()).thenReturn(overseasEntityDataApi);
        privateDataRetrievalService.getOverseasEntityData(REQUEST_ID, COMPANY_NUMBER);
        verify(apiClientService, times(1)).getInternalApiClient();
    }

    @Test
    void testGetOverseasEntityDataWhenURIValidationExceptionExceptionIsThrown() throws ApiErrorResponseException, URIValidationException {
        stubEmailClient();
        when(privateOverseasEntityDataGet.execute()).thenThrow(new URIValidationException(""));
        assertThrows(ServiceException.class, () -> privateDataRetrievalService.getOverseasEntityData(REQUEST_ID, COMPANY_NUMBER));
    }

    @Test
    void testGetOverseasEntityDataWhenApiErrorResponseExceptionIsThrown() throws ApiErrorResponseException, URIValidationException {
        stubEmailClient();
        when(privateOverseasEntityDataGet.execute()).thenThrow(new ApiErrorResponseException(builder));
        assertThrows(ServiceException.class, () -> privateDataRetrievalService.getOverseasEntityData(REQUEST_ID, COMPANY_NUMBER));
    }

    @Test
    void testGetCompanyProfileDataWhenSuccessful() throws ApiErrorResponseException, URIValidationException, ServiceException {
        CompanyProfileApi companyProfileApi = new CompanyProfileApi();
        stubCompanyProfileClient();
        when(companyGet.execute()).thenReturn(companyProfileApiResponse);
        when(companyProfileApiResponse.getData()).thenReturn(companyProfileApi);
        privateDataRetrievalService.getCompanyProfile(REQUEST_ID, COMPANY_NUMBER);
        verify(apiClientService, times(1)).getInternalApiClient();
    }

    @Test
    void testGetCompanyProfileDataWhenURIValidationExceptionExceptionIsThrown() throws ApiErrorResponseException, URIValidationException {
        stubCompanyProfileClient();
        when(companyGet.execute()).thenThrow(new ApiErrorResponseException(builder));
        assertThrows(ServiceException.class, () -> privateDataRetrievalService.getCompanyProfile(REQUEST_ID, COMPANY_NUMBER));
    }

    @Test
    void testGetCompanyProfileDataWhenApiErrorResponseExceptionIsThrown() throws ApiErrorResponseException, URIValidationException {
        stubCompanyProfileClient();
        when(companyGet.execute()).thenThrow(new ApiErrorResponseException(builder));
        assertThrows(ServiceException.class, () -> privateDataRetrievalService.getCompanyProfile(REQUEST_ID, COMPANY_NUMBER));
    }

    private void stubEmailClient() {
        when(internalApiClient.privateOverseasEntityDataHandler()).thenReturn(privateOverseasEntityDataHandler);
        when(privateOverseasEntityDataHandler.getOverseasEntityData(anyString())).thenReturn(privateOverseasEntityDataGet);
    }

    private void stubCompanyProfileClient() {
        when(internalApiClient.company()).thenReturn(companyResourceHandler);
        when(companyResourceHandler.get(any())).thenReturn(companyGet);
    }
}
