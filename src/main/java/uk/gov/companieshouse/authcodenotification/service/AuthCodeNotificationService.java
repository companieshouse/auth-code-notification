package uk.gov.companieshouse.authcodenotification.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.authcodenotification.exception.ServiceException;
import uk.gov.companieshouse.authcodenotification.utils.ApiLogger;
import uk.gov.companieshouse.logging.util.DataMap;

import java.util.Map;

@Service
public class AuthCodeNotificationService {

    private final PrivateDataRetrievalService privateDataRetrievalService;

    private final EmailService emailService;

    @Autowired
    public AuthCodeNotificationService(PrivateDataRetrievalService privateDataRetrievalService,
                                       EmailService emailService) {
        this.privateDataRetrievalService = privateDataRetrievalService;
        this.emailService = emailService;
    }

    public void sendAuthCodeEmail(String requestId, String authCode, String companyNumber) throws ServiceException {
        var dataMap = new DataMap.Builder().companyNumber(companyNumber).build();
        ApiLogger.infoContext(requestId, "Send auth code email invoked", dataMap.getLogMap());

        String email = getOverseasEntityEmail(requestId, companyNumber, dataMap.getLogMap());
        String companyName = getCompanyName(requestId, companyNumber, dataMap.getLogMap());

        emailService.sendAuthCodeEmail(requestId, authCode, companyName, companyNumber, email);
    }

    private String getOverseasEntityEmail(String requestId, String companyNumber, Map<String, Object> logMap) throws ServiceException {
        String email = privateDataRetrievalService.getOverseasEntityData(requestId, companyNumber).getEmail();

        if (StringUtils.isBlank(email)) {
            var e = new ServiceException("Null or empty email found");
            ApiLogger.errorContext(requestId, "Failed to retrieve a valid email", e, logMap);
            throw e;
        }

        ApiLogger.infoContext(requestId, "Retrieved auth code email successfully", logMap);
        return email;
    }

    private String getCompanyName(String requestId, String companyNumber, Map<String, Object> logMap) throws ServiceException {
        String companyName = privateDataRetrievalService.getCompanyProfile(requestId, companyNumber).getCompanyName();

        if (StringUtils.isBlank(companyName)) {
            var e = new ServiceException("Null or empty company name found");
            ApiLogger.errorContext(requestId, "Failed to retrieve a valid company number", e, logMap);
            throw e;
        }

        ApiLogger.infoContext(requestId, "Retrieved company name successfully", logMap);
        return companyName;
    }
}
