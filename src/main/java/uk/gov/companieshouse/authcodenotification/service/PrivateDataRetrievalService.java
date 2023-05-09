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

    public OverseasEntityDataApi getOverseasEntityData(String companyNumber)
            throws ServiceException {
        try {
            OverseasEntityDataApi overseasEntityDataApi = apiClientService
                    .getInternalApiClient()
                    .privateOverseasEntityDataHandler()
                    .getOverseasEntityData(OVERSEAS_ENTITY_URI_SECTION + companyNumber + "/entity-data")
                    .execute()
                    .getData();

            DataMap dataMap = new DataMap.Builder().companyNumber(companyNumber).build();
            ApiLogger.info("Retrieving overseas entity data for company number ",  dataMap.getLogMap());

            return overseasEntityDataApi;
        } catch (URIValidationException | IOException e) {
            var message = "Error Retrieving overseas entity data for " + companyNumber;
            ApiLogger.errorContext(message, e);
            throw new ServiceException(e.getMessage(), e);
        }
    }
}
