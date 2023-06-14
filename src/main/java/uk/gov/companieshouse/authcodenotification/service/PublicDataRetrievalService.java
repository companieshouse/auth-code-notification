package uk.gov.companieshouse.authcodenotification.service;

import com.google.api.client.http.HttpResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.authcodenotification.client.ApiClientService;
import uk.gov.companieshouse.authcodenotification.exception.EntityNotFoundException;
import uk.gov.companieshouse.authcodenotification.exception.ServiceException;
import uk.gov.companieshouse.authcodenotification.utils.ApiLogger;
import uk.gov.companieshouse.logging.util.DataMap;

import java.io.IOException;

@Service
public class PublicDataRetrievalService {

    private static final String COMPANY_PROFILE_URI = "/company/%s";

    @Autowired
    private ApiClientService apiClientService;

    public CompanyProfileApi getCompanyProfile(String requestId, String companyNumber)
            throws ServiceException {
        var dataMap = new DataMap.Builder().companyNumber(companyNumber).build();
        try {
            ApiLogger.infoContext(requestId, "Retrieving company profile data",  dataMap.getLogMap());

            var companyProfileApi = apiClientService
                    .getApiClient()
                    .company()
                    .get(String.format(COMPANY_PROFILE_URI, companyNumber))
                    .execute()
                    .getData();

            ApiLogger.infoContext(requestId, "Successfully retrieved company profile data",  dataMap.getLogMap());

            return companyProfileApi;
        } catch (URIValidationException | IOException e) {
            if (e instanceof HttpResponseException && ((HttpResponseException)e).getStatusCode() == 404) {
                var message = "Http exception status: " + ((HttpResponseException)e).getStatusCode();
                ApiLogger.errorContext(requestId, message, e, dataMap.getLogMap());
                throw new EntityNotFoundException(e.getMessage(), e);
            } else {
                var message = "Error retrieving company profile data";
                ApiLogger.errorContext(requestId, message, e, dataMap.getLogMap());
                throw new ServiceException(e.getMessage(), e);
            }
        }
    }
}
