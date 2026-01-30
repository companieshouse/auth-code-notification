package uk.gov.companieshouse.authcodenotification.service;

import java.util.Map;
import java.util.function.Supplier;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.ApiClient;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.authcodenotification.exception.EntityNotFoundException;
import uk.gov.companieshouse.authcodenotification.exception.ServiceException;
import uk.gov.companieshouse.authcodenotification.utils.ApiLogger;
import uk.gov.companieshouse.logging.util.DataMap;

@Service
public class PublicDataRetrievalService {

    private static final String COMPANY_PROFILE_URI = "/company/%s";

    private final Supplier<ApiClient> apiClientSupplier;

    public PublicDataRetrievalService(final Supplier<ApiClient> apiClientSupplier) {
        this.apiClientSupplier  = apiClientSupplier;
    }

    public CompanyProfileApi getCompanyProfile(String requestId, String companyNumber) throws ServiceException {
        var dataMap = new DataMap.Builder().companyNumber(companyNumber).build();
        try {
            ApiLogger.infoContext(requestId, "Retrieving company profile data",  dataMap.getLogMap());

            var companyProfileApi = apiClientSupplier
                    .get()
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
