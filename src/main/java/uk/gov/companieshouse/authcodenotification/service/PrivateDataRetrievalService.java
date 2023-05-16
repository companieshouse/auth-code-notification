package uk.gov.companieshouse.authcodenotification.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.api.model.update.OverseasEntityDataApi;
import uk.gov.companieshouse.authcodenotification.client.ApiClientService;
import uk.gov.companieshouse.authcodenotification.exception.ServiceException;
import uk.gov.companieshouse.authcodenotification.utils.ApiLogger;
import uk.gov.companieshouse.logging.util.DataMap;

import java.io.IOException;

@Service
public class PrivateDataRetrievalService {

    private static final String OVERSEAS_ENTITY_URI_SECTION = "/overseas-entity/%s/entity-data";

    private static final String COMPANY_PROFILE_URI = "/company/%s";

    @Autowired
    private ApiClientService apiClientService;

    public OverseasEntityDataApi getOverseasEntityData(String requestId, String companyNumber)
            throws ServiceException {
        var logDataMap = new DataMap.Builder().companyNumber(companyNumber).build();
        try {
            ApiLogger.infoContext(requestId, "Retrieving overseas entity data from database",  logDataMap.getLogMap());

            var overseasEntityDataApi = apiClientService
                    .getInternalApiClient()
                    .privateOverseasEntityDataHandler()
                    .getOverseasEntityData(String.format(OVERSEAS_ENTITY_URI_SECTION, companyNumber))
                    .execute()
                    .getData();

            ApiLogger.infoContext(requestId, "Successfully retrieved overseas entity data from database",  logDataMap.getLogMap());
            return overseasEntityDataApi;
        } catch (URIValidationException | IOException e) {
            var message = "Error retrieving overseas entity data from database";
            ApiLogger.errorContext(requestId, message, e, logDataMap.getLogMap());
            throw new ServiceException(e.getMessage(), e);
        }
    }

    public CompanyProfileApi getCompanyProfile(String requestId, String companyNumber)
            throws ServiceException {
        var dataMap = new DataMap.Builder().companyNumber(companyNumber).build();
        try {
            var companyProfileApi = apiClientService
                    .getInternalApiClient()
                    .company()
                    .get(String.format(COMPANY_PROFILE_URI, companyNumber))
                    .execute()
                    .getData();

            ApiLogger.infoContext(requestId, "Retrieving company profile data",  dataMap.getLogMap());

            return companyProfileApi;
        } catch (URIValidationException | IOException e) {
            var message = "Error retrieving company profile data";
            ApiLogger.errorContext(requestId, message, e, dataMap.getLogMap());
            throw new ServiceException(e.getMessage(), e);
        }
    }
}
