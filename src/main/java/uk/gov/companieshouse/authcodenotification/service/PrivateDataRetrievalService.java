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
        } catch (HttpResponseException httpe) {
            var message = "Http exception status: " + httpe.getStatusCode();
            ApiLogger.errorContext(requestId, message, httpe, logDataMap.getLogMap());
            if (httpe.getStatusCode() == 404) {
                throw new EntityNotFoundException(httpe.getMessage(), httpe);
            } else {
                throw new ServiceException(httpe.getMessage(), httpe);
            }
        } catch (URIValidationException | IOException e) {
            var message = "Error retrieving overseas entity data from database";
            ApiLogger.errorContext(requestId, message, e, logDataMap.getLogMap());
            throw new ServiceException(e.getMessage(), e);
        }
    }
}
