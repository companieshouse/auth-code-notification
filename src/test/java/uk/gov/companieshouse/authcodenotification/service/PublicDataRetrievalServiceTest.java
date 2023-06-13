package uk.gov.companieshouse.authcodenotification.service;

import com.google.api.client.http.HttpResponseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.ApiClient;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.company.CompanyResourceHandler;
import uk.gov.companieshouse.api.handler.company.request.CompanyGet;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.authcodenotification.client.ApiClientService;
import uk.gov.companieshouse.authcodenotification.exception.EntityNotFoundException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PublicDataRetrievalServiceTest {

    private static final String REQUEST_ID = "abc";

    private static final String COMPANY_NUMBER = "OE123456";

    private static final String COMPANY_PROFILE_URI = String.format("/company/%s", COMPANY_NUMBER);


    @InjectMocks
    private PublicDataRetrievalService publicDataRetrievalService;

    @Mock
    private ApiClientService apiClientService;

    @Mock
    private ApiClient apiClient;

    @Mock
    private CompanyResourceHandler companyResourceHandler;

    @Mock
    private CompanyGet companyGet;

    @Mock
    private ApiResponse<CompanyProfileApi> companyProfileApiResponse;

    @Mock
    private HttpResponseException.Builder builder;

    @BeforeEach
    void setup() {
        when(apiClientService.getApiClient()).thenReturn(apiClient);
        when(apiClient.company()).thenReturn(companyResourceHandler);
        when(companyResourceHandler.get(COMPANY_PROFILE_URI)).thenReturn(companyGet);
    }

    @Test
    void testGetCompanyProfileDataWhenSuccessful() throws IOException, URIValidationException, EntityNotFoundException {
        CompanyProfileApi companyProfileApi = new CompanyProfileApi();
        when(companyGet.execute()).thenReturn(companyProfileApiResponse);
        when(companyProfileApiResponse.getData()).thenReturn(companyProfileApi);

        CompanyProfileApi returnedCompanyProfileApi = publicDataRetrievalService.getCompanyProfile(REQUEST_ID, COMPANY_NUMBER);

        assertEquals(companyProfileApi, returnedCompanyProfileApi);
        verify(apiClientService, times(1)).getApiClient();
    }

    @Test
    void testGetCompanyProfileDataWhenURIValidationExceptionExceptionIsThrown() throws IOException, URIValidationException {
        when(companyGet.execute()).thenThrow(new ApiErrorResponseException(builder));
        assertThrows(EntityNotFoundException.class, () -> publicDataRetrievalService.getCompanyProfile(REQUEST_ID, COMPANY_NUMBER));
    }

    @Test
    void testGetCompanyProfileDataWhenApiErrorResponseExceptionIsThrown() throws IOException, URIValidationException {
        when(companyGet.execute()).thenThrow(new ApiErrorResponseException(builder));
        assertThrows(EntityNotFoundException.class, () -> publicDataRetrievalService.getCompanyProfile(REQUEST_ID, COMPANY_NUMBER));
    }

}
