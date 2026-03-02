package uk.gov.companieshouse.authcodenotification.service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.authcodenotification.email.EmailClient;
import uk.gov.companieshouse.authcodenotification.email.EmailContent;
import uk.gov.companieshouse.authcodenotification.email.EmailData;
import uk.gov.companieshouse.authcodenotification.utils.ApiLogger;
import uk.gov.companieshouse.logging.util.DataMap;

@Service
public class EmailService {

    @Value("${application.email-service.subject}")
    private String emailSubject;

    @Value("${application.email-service.sender-app-id}")
    private String originatingAppId;

    @Value("${application.email-service.overseas-entities-template}")
    private String overseasEntitiesTemplate;

    private final EmailClient emailClient;
    private final Supplier<LocalDateTime> dateTimeSupplier;

    @Autowired
    public EmailService(final EmailClient emailClient, final Supplier<LocalDateTime> dateTimeSupplier) {
        this.emailClient = emailClient;
        this.dateTimeSupplier = dateTimeSupplier;
    }

    public void sendAuthCodeEmail(String requestId, String authCode, String companyName, String companyNumber, String emailAddress) {
//        var emailData = new EmailData(emailSubject, emailAddress, authCode, companyName, companyNumber);
        var emailData = new EmailData(emailSubject, "jbishop2@companieshouse.gov.uk", authCode, companyName, companyNumber);

        var emailContent = new EmailContent.Builder()
                .withOriginatingAppId(originatingAppId)
                .withMessageType(overseasEntitiesTemplate)
                .withMessageId(UUID.randomUUID().toString())
                .withData(emailData)
                .withEmailAddress(emailAddress)
                .withCreatedAt(dateTimeSupplier.get())
                .build();

        var logDataMap = new DataMap.Builder().companyNumber(companyNumber).build();
        ApiLogger.infoContext(requestId, "Calling CHS Kafka API client to send auth code email", logDataMap.getLogMap());

        emailClient.sendEmail(requestId, emailContent);

        ApiLogger.infoContext(requestId, "Successfully called CHS Kafka API client to send auth code email", logDataMap.getLogMap());
    }

}
