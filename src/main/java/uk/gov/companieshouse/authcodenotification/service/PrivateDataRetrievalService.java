package uk.gov.companieshouse.authcodenotification.service;

import java.util.Map;
import java.util.function.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.company.RegisteredEmailAddressJson;
import uk.gov.companieshouse.authcodenotification.config.ApiClientConfig;
import uk.gov.companieshouse.authcodenotification.exception.EntityNotFoundException;
import uk.gov.companieshouse.authcodenotification.exception.ServiceException;
import uk.gov.companieshouse.authcodenotification.utils.ApiLogger;
import uk.gov.companieshouse.logging.util.DataMap;

@Service
public class PrivateDataRetrievalService {

    private static final String REGISTERED_EMAIL_ADDRESS_URI_SUFFIX = "/company/%s/registered-email-address";

    private final Supplier<InternalApiClient> internalApiClient;

    public PrivateDataRetrievalService(final Supplier<InternalApiClient> internalApiClient) {
        this.internalApiClient = internalApiClient;
    }

    public RegisteredEmailAddressJson getCompanyRegisteredEmailAddress(String requestId, String companyNumber) throws ServiceException {
        var logDataMap = new DataMap.Builder().companyNumber(companyNumber).build();
        try {
            ApiLogger.infoContext(requestId, "Retrieving company registered email address from database", logDataMap.getLogMap());

            var registeredEmailAddress = internalApiClient
                    .get()
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
