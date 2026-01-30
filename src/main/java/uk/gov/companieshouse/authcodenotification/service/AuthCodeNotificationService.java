package uk.gov.companieshouse.authcodenotification.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.authcodenotification.exception.ServiceException;
import uk.gov.companieshouse.authcodenotification.utils.ApiLogger;
import uk.gov.companieshouse.authcodenotification.utils.Encrypter;
import uk.gov.companieshouse.logging.util.DataMap;

import java.util.Map;

@Service
public class AuthCodeNotificationService {

    private static final String BLANK_ENCRYPTED_AUTH_CODE_MESSAGE = "Encrypted auth code is blank";
    public static final String ENCRYPTION_ERROR_MESSAGE = "Error when encrypting text";

    @Value("${application.data-field.encryption-key}")
    private String aesKeyString;

    private final PrivateDataRetrievalService privateDataRetrievalService;

    private final PublicDataRetrievalService publicDataRetrievalService;

    private final EmailService emailService;

    private final Encrypter encrypter;

    @Autowired
    public AuthCodeNotificationService(PrivateDataRetrievalService privateDataRetrievalService,
                                       PublicDataRetrievalService publicDataRetrievalService,
                                       EmailService emailService,
                                       Encrypter encrypter) {
        this.privateDataRetrievalService = privateDataRetrievalService;
        this.publicDataRetrievalService = publicDataRetrievalService;
        this.emailService = emailService;
        this.encrypter = encrypter;
    }

    public void sendAuthCodeEmail(String requestId, String authCode, String companyNumber) throws ServiceException {
        var logDataMap = new DataMap.Builder().companyNumber(companyNumber).build();
        ApiLogger.infoContext(requestId, "Processing send auth code email request", logDataMap.getLogMap());
        var logMap = logDataMap.getLogMap();

        String encryptedAuthCode = encryptAuthCode(requestId, authCode, logMap);
        String emailAddress = getOverseasEntityEmailAddress(requestId, companyNumber, logMap);
        String companyName = getCompanyName(requestId, companyNumber, logMap);
        emailService.sendAuthCodeEmail(requestId, encryptedAuthCode, companyName, companyNumber, emailAddress);

        ApiLogger.infoContext(requestId, "Finished processing send auth code email request", logDataMap.getLogMap());
    }

    private String encryptAuthCode(String requestId, String authCode, Map<String, Object> logMap) throws ServiceException {
        try {
            String encryptedAuthCode = encrypter.encrypt(authCode, aesKeyString);
            if (StringUtils.isBlank(encryptedAuthCode)) {
                ApiLogger.errorContext(requestId, BLANK_ENCRYPTED_AUTH_CODE_MESSAGE, null, logMap);
                throw new ServiceException(BLANK_ENCRYPTED_AUTH_CODE_MESSAGE);
            }
            ApiLogger.infoContext(requestId, "Successfully encrypted auth code to " + encryptedAuthCode, logMap);

            return encryptedAuthCode;
        } catch (Exception e) {
            ApiLogger.errorContext(requestId, ENCRYPTION_ERROR_MESSAGE, e, logMap);
            throw new ServiceException(ENCRYPTION_ERROR_MESSAGE, e);
        }
    }

    private String getOverseasEntityEmailAddress(String requestId, String companyNumber, Map<String, Object> logMap) throws ServiceException {
        String emailAddress = privateDataRetrievalService.getCompanyRegisteredEmailAddress(requestId, companyNumber).getRegisteredEmailAddress();

        if (StringUtils.isBlank(emailAddress)) {
            ApiLogger.errorContext(requestId, "Failed to retrieve a valid overseas entity email address", null, logMap);
            throw new ServiceException("Null or empty email found");
        }

        ApiLogger.infoContext(requestId, "Successfully retrieved overseas entity email address", logMap);
        return emailAddress;
    }

    private String getCompanyName(String requestId, String companyNumber, Map<String, Object> logMap) throws ServiceException {
        String companyName = publicDataRetrievalService.getCompanyProfile(requestId, companyNumber).getCompanyName();

        if (StringUtils.isBlank(companyName)) {
            ApiLogger.errorContext(requestId, "Failed to retrieve a valid company number", null, logMap);
            throw new ServiceException("Null or empty company name found");
        }

        ApiLogger.infoContext(requestId, "Successfully retrieved company name", logMap);
        return companyName;
    }
}
