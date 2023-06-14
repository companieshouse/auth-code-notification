package uk.gov.companieshouse.authcodenotification.service;

import com.google.api.client.http.HttpResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.update.OverseasEntityDataApi;
import uk.gov.companieshouse.authcodenotification.client.ApiClientService;
import uk.gov.companieshouse.authcodenotification.exception.EntityNotFoundException;
import uk.gov.companieshouse.authcodenotification.exception.ServiceException;
import uk.gov.companieshouse.authcodenotification.utils.ApiLogger;
import uk.gov.companieshouse.logging.util.DataMap;

import java.io.IOException;

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
        } catch (URIValidationException | IOException e) {
            if (e instanceof HttpResponseException && ((HttpResponseException)e).getStatusCode() == 404) {
                var message = "Http exception status: " + ((HttpResponseException)e).getStatusCode();
                ApiLogger.errorContext(requestId, message, e, logDataMap.getLogMap());
                throw new EntityNotFoundException(e.getMessage(), e);
            } else {
                var message = "Error retrieving overseas entity data from database";
                ApiLogger.errorContext(requestId, message, e, logDataMap.getLogMap());
                throw new ServiceException(e.getMessage(), e);
            }
        }
    }
}
