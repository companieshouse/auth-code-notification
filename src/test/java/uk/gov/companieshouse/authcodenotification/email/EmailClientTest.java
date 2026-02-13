package uk.gov.companieshouse.authcodenotification.email;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpResponseException.Builder;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.chskafka.SendEmail;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.chskafka.PrivateSendEmailHandler;
import uk.gov.companieshouse.api.handler.chskafka.request.PrivateSendEmailPost;
import uk.gov.companieshouse.api.http.HttpClient;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.authcodenotification.exception.EmailClientException;

@ExtendWith(MockitoExtension.class)
public class EmailClientTest {

    private static final String REQUEST_ID = "requestId";
    private static final String APP_ID = "app_id";
    private static final Supplier<LocalDateTime> dateTimeNow = LocalDateTime::now;

    private static final String TEMPLATE_NAME = "template_name";
    private static final String EMAIL_ADDRESS = "test@chtest.gov.uk";
    private static final String MESSAGE_ID = UUID.randomUUID().toString();

    @Mock
    Supplier<InternalApiClient> apiClientSupplier;

    @Mock
    ObjectMapper objectMapper;

    @Mock
    InternalApiClient apiClient;

    @Mock
    HttpClient httpClient;

    @Mock
    PrivateSendEmailHandler privateSendEmailHandler;

    @Mock
    PrivateSendEmailPost privateSendEmailPost;

    EmailClient underTest;

    @BeforeEach
    void setUp() {
        underTest = new EmailClient(apiClientSupplier, objectMapper);
    }

    private EmailContent constructEmailContent(final Map<String, Object> data) {
        return new EmailContent.Builder()
                .withOriginatingAppId(APP_ID)
                .withCreatedAt(dateTimeNow.get())
                .withMessageType(TEMPLATE_NAME)
                .withMessageId(MESSAGE_ID)
                .withEmailAddress(EMAIL_ADDRESS)
                .withData(data)
                .build();
    }

    @Test
    public void givenValidContent_whenSendEmailCalled_thenResponseOk() throws JsonProcessingException, ApiErrorResponseException {
        Map<String, Object> data = new HashMap<>();
        data.put("subject", "This is the email subject");
        data.put("to", "sendto@emailaddress.com");
        data.put("auth_code", "OU812");
        data.put("company_name", "My Company Name");
        data.put("company_number", "00006400");

        EmailContent content = constructEmailContent(data);

        when(objectMapper.writeValueAsString(any(Map.class))).thenReturn("{\"json\": \"data\"}");
        when(apiClientSupplier.get()).thenReturn(apiClient);
        when(apiClient.getHttpClient()).thenReturn(httpClient);
        when(apiClient.sendEmailHandler()).thenReturn(privateSendEmailHandler);
        when(privateSendEmailHandler.postSendEmail(eq("/send-email"), any(SendEmail.class))).thenReturn(privateSendEmailPost);
        when(privateSendEmailPost.execute()).thenReturn(new ApiResponse<>(201, null, null));

        ApiResponse<Void> response = underTest.sendEmail(REQUEST_ID, content);

        verify(apiClientSupplier, times(1)).get();
        verify(apiClient, times(2)).getHttpClient();
        verify(apiClient, times(1)).sendEmailHandler();
        verify(privateSendEmailHandler, times(1)).postSendEmail(eq("/send-email"), any(SendEmail.class));
        verify(privateSendEmailPost, times(1)).execute();

        assertThat(response, is(notNullValue()));
        assertThat(response.getStatusCode(), is(201));
    }

    @Test
    public void givenBadRequest_whenSendEmailCalled_thenExceptionRaised() throws JsonProcessingException, ApiErrorResponseException {
        Map<String, Object> data = new HashMap<>();
        data.put("subject", "This is the email subject");
        data.put("to", "sendto@emailaddress.com");
        data.put("auth_code", "OU812");
        data.put("company_name", "My Company Name");
        data.put("company_number", "00006400");

        EmailContent content = constructEmailContent(data);

        when(objectMapper.writeValueAsString(any(Map.class))).thenReturn("{\"json\": \"data\"}");
        when(apiClientSupplier.get()).thenReturn(apiClient);
        when(apiClient.getHttpClient()).thenReturn(httpClient);
        when(apiClient.sendEmailHandler()).thenReturn(privateSendEmailHandler);
        when(privateSendEmailHandler.postSendEmail(eq("/send-email"), any(SendEmail.class))).thenReturn(privateSendEmailPost);

        ApiErrorResponseException apiErrorResponseException = ApiErrorResponseException.fromHttpResponseException(
                new Builder(400, "Bad Request", new HttpHeaders()).build());

        when(privateSendEmailPost.execute()).thenThrow(apiErrorResponseException);

        EmailClientException expectedException = assertThrows(EmailClientException.class, () ->
                underTest.sendEmail(REQUEST_ID, content)
        );

        verify(apiClientSupplier, times(1)).get();
        verify(apiClient, times(1)).getHttpClient();
        verify(apiClient, times(1)).sendEmailHandler();
        verify(privateSendEmailHandler, times(1)).postSendEmail(eq("/send-email"), any(SendEmail.class));
        verify(privateSendEmailPost, times(1)).execute();

        assertThat(expectedException, is(notNullValue()));
        assertThat(expectedException.getMessage(), is("Error sending payload to CHS Kafka API: "));
    }


    @Test
    public void givenParsingError_whenSendEmailCalled_thenRaisedException() throws JsonProcessingException, ApiErrorResponseException {
        Map<String, Object> data = new HashMap<>();
        data.put("subject", "This is the email subject");
        data.put("to", "sendto@emailaddress.com");
        data.put("auth_code", "OU812");
        data.put("company_name", "My Company Name");
        data.put("company_number", "00006400");

        EmailContent content = constructEmailContent(data);

        when(objectMapper.writeValueAsString(any(Map.class))).thenThrow(new JsonProcessingException("Error parsing JSON") {});

        EmailClientException expectedException = assertThrows(EmailClientException.class, () ->
                underTest.sendEmail(REQUEST_ID, content)
        );

        verify(objectMapper, times(1)).writeValueAsString(any(Map.class));
        verifyNoInteractions(apiClientSupplier);
        verifyNoInteractions(apiClient);
        verifyNoInteractions(httpClient);
        verifyNoInteractions(privateSendEmailHandler);
        verifyNoInteractions(privateSendEmailPost);

        assertThat(expectedException, is(notNullValue()));
        assertThat(expectedException.getMessage(), is("Error creating payload for CHS Kafka API: "));
    }
}
