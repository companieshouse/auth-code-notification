package uk.gov.companieshouse.authcodenotification.service;

import org.springframework.beans.factory.annotation.Value;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.company.RegisteredEmailAddressJson;
import uk.gov.companieshouse.authcodenotification.client.ApiClientService;
import uk.gov.companieshouse.authcodenotification.exception.EntityNotFoundException;
import uk.gov.companieshouse.authcodenotification.exception.ServiceException;
import uk.gov.companieshouse.authcodenotification.utils.ApiLogger;
import uk.gov.companieshouse.logging.util.DataMap;

import java.util.Map;

@Service
public class PrivateDataRetrievalService {

    private static final String REGISTERED_EMAIL_ADDRESS_URI_SUFFIX = "/company/%s/registered-email-address";

    @Autowired
    private ApiClientService apiClientService;

    @Value("${ORACLE_QUERY_API_URL}")
    private String oracleQueryApiUrl;

    public RegisteredEmailAddressJson getCompanyRegisteredEmailAddress(String requestId, String companyNumber)
            throws ServiceException {
        var logDataMap = new DataMap.Builder().companyNumber(companyNumber).build();
        try {
            ApiLogger.infoContext(requestId, "Retrieving company registered email address from database", logDataMap.getLogMap());

            var internalApiClient = apiClientService.getInternalApiClient();
            internalApiClient.setBasePath(oracleQueryApiUrl);
            var registeredEmailAddress = internalApiClient
                    .privateCompanyResourceHandler()
                    .getCompanyRegisteredEmailAddress(String.format(REGISTERED_EMAIL_ADDRESS_URI_SUFFIX, companyNumber))
                    .execute()
                    .getData();

            ApiLogger.infoContext(requestId, "Successfully retrieved company registered email address from database", logDataMap.getLogMap());
            return registeredEmailAddress;
        } catch (ApiErrorResponseException e) {
            if (e.getStatusCode() == 404) {
                var message = "Unable to find company registered email address from database, HTTP exception status: " + e.getStatusCode();
                ApiLogger.errorContext(requestId, message, e, logDataMap.getLogMap());
                throw new EntityNotFoundException(e.getMessage(), e);
            } else {
                throw buildServiceException(requestId, e, logDataMap.getLogMap());
            }
        } catch (URIValidationException e) {
            throw buildServiceException(requestId, e, logDataMap.getLogMap());
        }
    }

    private ServiceException buildServiceException(String requestId, Exception e, Map<String, Object> logMap) {
        ApiLogger.errorContext(requestId, "Error retrieving company registered email address from database", e, logMap);
        return new ServiceException(e.getMessage(), e);
    }
}
