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
        var logDataMap = new DataMap.Builder().companyNumber(companyNumber).build();
        ApiLogger.infoContext(requestId, "Processing send auth code email request", logDataMap.getLogMap());

        String email = getOverseasEntityEmail(requestId, companyNumber, logDataMap.getLogMap());

        // TODO Lookup company name
        emailService.sendAuthCodeEmail(requestId, authCode, "", companyNumber, email);

        ApiLogger.infoContext(requestId, "Finished processing send auth code email request", logDataMap.getLogMap());
    }

    private String getOverseasEntityEmail(String requestId, String companyNumber, Map<String, Object> logMap) throws ServiceException {
        String email = privateDataRetrievalService.getOverseasEntityData(requestId, companyNumber).getEmail();

        if (StringUtils.isBlank(email)) {
            var e = new ServiceException("Null or empty email found");
            ApiLogger.errorContext(requestId, "Failed to retrieve a valid overseas entity email address", e, logMap);
            throw e;
        }

        ApiLogger.infoContext(requestId, "Successfully retrieved overseas entity email address", logMap);
        return email;
    }

}
