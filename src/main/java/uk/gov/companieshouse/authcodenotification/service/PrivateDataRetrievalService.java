package uk.gov.companieshouse.authcodenotification.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.update.OverseasEntityDataApi;
import uk.gov.companieshouse.authcodenotification.client.ApiClientService;
import uk.gov.companieshouse.authcodenotification.exception.ServiceException;
import uk.gov.companieshouse.authcodenotification.utils.ApiLogger;
import uk.gov.companieshouse.logging.util.DataMap;

import java.io.IOException;

@Service
public class PrivateDataRetrievalService {

    private static final String OVERSEAS_ENTITY_URI_SECTION = "/overseas-entity/";

    @Autowired
    private ApiClientService apiClientService;

    public OverseasEntityDataApi getOverseasEntityData(String requestId, String companyNumber)
            throws ServiceException {
        DataMap dataMap = new DataMap.Builder().companyNumber(companyNumber).build();
        try {
            OverseasEntityDataApi overseasEntityDataApi = apiClientService
                    .getInternalApiClient()
                    .privateOverseasEntityDataHandler()
                    .getOverseasEntityData(OVERSEAS_ENTITY_URI_SECTION + companyNumber + "/entity-data")
                    .execute()
                    .getData();

            ApiLogger.infoContext(requestId, "Retrieving overseas entity data",  dataMap.getLogMap());

            return overseasEntityDataApi;
        } catch (URIValidationException | IOException e) {
            var message = "Error retrieving overseas entity data";
            ApiLogger.errorContext(requestId, message, e, dataMap.getLogMap());
            throw new ServiceException(e.getMessage(), e);
        }
    }
}
