package uk.gov.companieshouse.authcodenotification.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.authcodenotification.email.EmailContent;
import uk.gov.companieshouse.authcodenotification.email.KafkaEmailClient;
import uk.gov.companieshouse.authcodenotification.exception.ServiceException;
import uk.gov.companieshouse.authcodenotification.utils.ApiLogger;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

@Service
public class EmailService {
    private final KafkaEmailClient kafkaEmailClient;
    private final Supplier<LocalDateTime> dateTimeSupplier;

    @Value("${EMAIL_SUBJECT}")
    private String emailSubject;

    @Value("${EMAIL_SENDER_APP_ID}")
    private String originatingAppId;

    @Value("${EMAIL_AUTH_CODE_TEMPLATE}")
    private String authCodeEmailTemplate;


    @Autowired
    public EmailService(
            KafkaEmailClient kafkaEmailClient,
            Supplier<LocalDateTime> dateTimeSupplier
    ) {
        this.kafkaEmailClient = kafkaEmailClient;
        this.dateTimeSupplier = dateTimeSupplier;
    }

    public void sendAuthCodeEmail(
            String requestId,
            String authCode,
            String companyName,
            String companyNumber,
            String emailAddress
    ) throws ServiceException {

        Map<String, Object> data = constructCommonEmailMap(
              authCode, companyName, companyNumber, emailAddress);

        EmailContent emailContent = constructEmailContent(authCodeEmailTemplate,
                emailAddress, data);

        ApiLogger.debugContext(requestId, "Calling Kafka client to send auth code email");
        kafkaEmailClient.sendEmailToKafka(requestId, emailContent);
        ApiLogger.debugContext(requestId, "Successfully called Kafka client");
    }


    private EmailContent constructEmailContent(String templateName,
                                               String emailAddress,
                                               Map<String, Object> data) {
        return new EmailContent.Builder()
                .withOriginatingAppId(originatingAppId)
                .withCreatedAt(dateTimeSupplier.get())
                .withMessageType(templateName)
                .withMessageId(UUID.randomUUID().toString())
                .withEmailAddress(emailAddress)
                .withData(data)
                .build();
    }

    private Map<String, Object> constructCommonEmailMap(String authCode,
                                                        String companyName,
                                                        String companyNumber,
                                                        String emailAddress) {
        Map<String, Object> data = new HashMap<>();

        data.put("subject", emailSubject);
        data.put("to", emailAddress);
        data.put("auth_code", authCode);
        data.put("company_name", companyName);
        data.put("company_number", companyNumber);
        return data;
    }
}
