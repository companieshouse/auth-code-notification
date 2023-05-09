package uk.gov.companieshouse.authcodenotification.service;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.authcodenotification.exception.ServiceException;
import uk.gov.companieshouse.authcodenotification.utils.ApiLogger;
import uk.gov.companieshouse.logging.util.DataMap;

@Service
public class AuthCodeNotificationService {

    @Autowired
    private PrivateDataRetrievalService privateDataRetrievalService;

    public void sendAuthCodeEmail(String requestId, String companyNumber) throws ServiceException {
        DataMap dataMap = new DataMap.Builder().companyNumber(companyNumber).build();
        ApiLogger.infoContext(requestId, "Send auth code email invoked", dataMap.getLogMap());

        String email = getOverseasEntityEmail(requestId, companyNumber);

        if (Strings.isNotBlank(email)) {
            ApiLogger.infoContext(requestId, "Retrieved auth code email successfully", dataMap.getLogMap());
        } else {
            ServiceException e = new ServiceException("Null or empty email found");
            ApiLogger.errorContext(requestId, "Failed to retrieve complete email", e, dataMap.getLogMap());
            throw e;
        }
        // 2. send email
    }

    private String getOverseasEntityEmail(String requestId, String companyNumber) throws ServiceException {
        return privateDataRetrievalService.getOverseasEntityData(requestId, companyNumber).getEmail();
    }

}
