package uk.gov.companieshouse.authcodenotification.service;

import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.authcodenotification.client.ApiClientService;
import uk.gov.companieshouse.authcodenotification.exception.EntityNotFoundException;
import uk.gov.companieshouse.authcodenotification.exception.ServiceException;
import uk.gov.companieshouse.authcodenotification.utils.ApiLogger;
import uk.gov.companieshouse.logging.util.DataMap;

import java.util.Map;

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
        } catch (ApiErrorResponseException e) {
            if (e.getStatusCode() == 404) {
                var message = "Unable to find company profile, HTTP exception status: " + e.getStatusCode();
                ApiLogger.errorContext(requestId, message, e, dataMap.getLogMap());
                throw new EntityNotFoundException(e.getMessage(), e);
            } else {
                throw buildServiceException(requestId, e, dataMap.getLogMap());
            }
        } catch (URIValidationException e) {
            throw buildServiceException(requestId, e, dataMap.getLogMap());
        }
    }

    private ServiceException buildServiceException(String requestId, Exception e, Map<String, Object> logMap) {
        ApiLogger.errorContext(requestId, "Error retrieving company profile data", e, logMap);
        return new ServiceException(e.getMessage(), e);
    }
}
