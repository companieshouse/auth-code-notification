package uk.gov.companieshouse.authcodenotification.email;

import static uk.gov.companieshouse.authcodenotification.AuthCodeNotificationApplication.APPLICATION_NAME_SPACE;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.function.Supplier;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.chskafka.SendEmail;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.authcodenotification.exception.EmailClientException;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Component
public class EmailClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(APPLICATION_NAME_SPACE);

    private final Supplier<InternalApiClient> internalApiClientSupplier;
    private final ObjectMapper objectMapper;

    public EmailClient(@Qualifier("chsKafkaApiClient") Supplier<InternalApiClient> supplier, ObjectMapper mapper) {
        this.internalApiClientSupplier = supplier;
        this.objectMapper = mapper;
    }

    public <T> ApiResponse<Void> sendEmail(final String requestId, final EmailContent content) throws EmailClientException {
        LOGGER.debug("sendEmail(requestId=%s, content=%s) method called.".formatted(requestId, content));

        try {
            var jsonData = objectMapper.writeValueAsString(content.getData());

            var sendEmail = new SendEmail();
            sendEmail.setAppId(content.getOriginatingAppId());
            sendEmail.setMessageId(content.getMessageId());
            sendEmail.setMessageType(content.getMessageType());
            sendEmail.setJsonData(jsonData);
            sendEmail.setEmailAddress(content.getEmailAddress());

            var apiClient = internalApiClientSupplier.get();
            apiClient.getHttpClient().setRequestId(requestId);

            var emailHandler = apiClient.sendEmailHandler();
            var emailPost = emailHandler.postSendEmail("/send-email", sendEmail);

            ApiResponse<Void> response = emailPost.execute();

            LOGGER.info(String.format("Posted '%s' email to CHS Kafka API (RequestId: %s): (Response %d)",
                    sendEmail.getMessageType(), apiClient.getHttpClient().getRequestId(), response.getStatusCode()));

            return response;

        } catch(JsonProcessingException ex) {
            LOGGER.error("Error creating payload", ex);
            throw new EmailClientException("Error creating payload for CHS Kafka API: ", ex);

        } catch (ApiErrorResponseException ex) {
            LOGGER.error("Error sending email", ex);
            throw new EmailClientException("Error sending payload to CHS Kafka API: ", ex);
        }
    }
}
