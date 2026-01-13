package uk.gov.companieshouse.authcodenotification.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.authcodenotification.email.EmailContent;
import uk.gov.companieshouse.authcodenotification.email.KafkaEmailClient;
import uk.gov.companieshouse.authcodenotification.exception.ServiceException;
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

    private final KafkaEmailClient kafkaEmailClient;
    private final Supplier<LocalDateTime> dateTimeSupplier;

    @Autowired
    public EmailService(final KafkaEmailClient kafkaEmailClient, final Supplier<LocalDateTime> dateTimeSupplier) {
        this.kafkaEmailClient = kafkaEmailClient;
        this.dateTimeSupplier = dateTimeSupplier;
    }

    public void sendAuthCodeEmail(String requestId, String authCode, String companyName, String companyNumber, String emailAddress)
            throws ServiceException {

        Map<String, Object> emailContentData = constructCommonEmailMap(
              authCode, companyName, companyNumber, emailAddress);

        var emailContent = constructEmailContent(overseasEntitiesTemplate, emailAddress, emailContentData);
        var logDataMap = new DataMap.Builder().companyNumber(companyNumber).build();

        ApiLogger.infoContext(requestId, "Calling Kafka client to send auth code email", logDataMap.getLogMap());
        kafkaEmailClient.sendEmailToKafka(requestId, emailContent);

        ApiLogger.infoContext(requestId, "Successfully called Kafka client to send auth code email", logDataMap.getLogMap());
    }


    private EmailContent constructEmailContent(String templateName, String emailAddress, Map<String, Object> data) {
        return new EmailContent.Builder()
                .withOriginatingAppId(originatingAppId)
                .withCreatedAt(dateTimeSupplier.get())
                .withMessageType(templateName)
                .withMessageId(UUID.randomUUID().toString())
                .withEmailAddress(emailAddress)
                .withData(data)
                .build();
    }

    private Map<String, Object> constructCommonEmailMap(String authCode, String companyName, String companyNumber, String emailAddress) {
        Map<String, Object> data = new HashMap<>();
        data.put("subject", emailSubject);
        data.put("to", emailAddress);
        data.put("auth_code", authCode);
        data.put("company_name", companyName);
        data.put("company_number", companyNumber);

        return data;
    }
}
