package uk.gov.companieshouse.authcodenotification.service;

import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.update.OverseasEntityDataApi;
import uk.gov.companieshouse.authcodenotification.client.ApiClientService;
import uk.gov.companieshouse.authcodenotification.exception.EntityNotFoundException;
import uk.gov.companieshouse.authcodenotification.exception.ServiceException;
import uk.gov.companieshouse.authcodenotification.utils.ApiLogger;
import uk.gov.companieshouse.logging.util.DataMap;

import java.util.Map;

@Service
public class PrivateDataRetrievalService {

    private static final String OVERSEAS_ENTITY_URI_SECTION = "/overseas-entity/%s/entity-data";

    @Autowired
    private ApiClientService apiClientService;

    public OverseasEntityDataApi getOverseasEntityData(String requestId, String companyNumber)
            throws ServiceException {
        var logDataMap = new DataMap.Builder().companyNumber(companyNumber).build();
        try {
            ApiLogger.infoContext(requestId, "Retrieving overseas entity data from database", logDataMap.getLogMap());

            var overseasEntityDataApi = apiClientService
                    .getInternalApiClient()
                    .privateOverseasEntityDataHandler()
                    .getOverseasEntityData(String.format(OVERSEAS_ENTITY_URI_SECTION, companyNumber))
                    .execute()
                    .getData();

            ApiLogger.infoContext(requestId, "Successfully retrieved overseas entity data from database", logDataMap.getLogMap());
            return overseasEntityDataApi;
        } catch (ApiErrorResponseException e) {
            if (e.getStatusCode() == 404) {
                var message = "HTTP exception status: " + e.getStatusCode();
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
        ApiLogger.errorContext(requestId, "Error retrieving overseas entity data from database", e, logMap);
        return new ServiceException(e.getMessage(), e);
    }
}
