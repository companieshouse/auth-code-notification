package uk.gov.companieshouse.authcodenotification.service;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.authcodenotification.exception.ServiceException;
import uk.gov.companieshouse.authcodenotification.utils.ApiLogger;
import uk.gov.companieshouse.logging.util.DataMap;

import java.util.Map;

@Service
public class AuthCodeNotificationService {

    @Autowired
    private PrivateDataRetrievalService privateDataRetrievalService;

    public void sendAuthCodeEmail(String requestId, String companyNumber) throws ServiceException {
        DataMap dataMap = new DataMap.Builder().companyNumber(companyNumber).build();
        ApiLogger.infoContext(requestId, "Send auth code email invoked", dataMap.getLogMap());

        String email = getOverseasEntityEmail(requestId, companyNumber, dataMap.getLogMap());

        // TODO send email
    }

    private String getOverseasEntityEmail(String requestId, String companyNumber, Map<String, Object> logMap) throws ServiceException {
        String email = privateDataRetrievalService.getOverseasEntityData(requestId, companyNumber).getEmail();

        if (Strings.isBlank(email)) {
            ServiceException e = new ServiceException("Null or empty email found");
            ApiLogger.errorContext(requestId, "Failed to retrieve a valid email", e, logMap);
            throw e;
        }

        ApiLogger.infoContext(requestId, "Retrieved auth code email successfully", logMap);
        return email;
    }

}
