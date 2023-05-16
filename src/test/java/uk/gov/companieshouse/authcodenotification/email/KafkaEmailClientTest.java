package uk.gov.companieshouse.authcodenotification.email;

import org.apache.avro.Schema;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.authcodenotification.exception.ServiceException;
import uk.gov.companieshouse.kafka.producer.CHKafkaProducer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.companieshouse.authcodenotification.TestUtils.buildEmailContent;
import static uk.gov.companieshouse.authcodenotification.TestUtils.getDummyEmailData;
import static uk.gov.companieshouse.authcodenotification.TestUtils.getDummySchema;

@ExtendWith(MockitoExtension.class)
class KafkaEmailClientTest {

    private static final LocalDateTime CREATED_AT = LocalDateTime.of(2019, 1, 1, 0, 0);

    private static final String MESSAGE_ID = "abc";
    private static final String NO_LONGER_REQUIRED_TEMPLATE_MESSAGE_TYPE = "promise_to_file_no_longer_required";
    private static final String EMAIL_NO_LONGER_REQUIRED_TEMPLATE_APP_ID = "filing_processed_notification_sender.promise_to_file_no_longer_required";
    private static final String CUSTOMER_EMAIL = "example@test.co.uk";
    private static final String REQUEST_ID = "54321asd";

    private KafkaEmailClient kafkaEmailClient;
    private Schema testSchema;
    private EmailContent emailContent;

    @Mock
    private Future<RecordMetadata> future;

    @Mock
    private CHKafkaProducer producer;

    @Mock
    private AvroSerializer avroSerializer;

    @Mock
    private AvroSerializer faultyAvroSerializer;

    @BeforeEach
    void setup() throws IOException {
        emailContent = buildEmailContent(
                EMAIL_NO_LONGER_REQUIRED_TEMPLATE_APP_ID,
                MESSAGE_ID,
                NO_LONGER_REQUIRED_TEMPLATE_MESSAGE_TYPE,
                getDummyEmailData(),
                CUSTOMER_EMAIL,
                CREATED_AT);

        testSchema = getDummySchema(this.getClass().getClassLoader().getResource("email/email-send.avsc"));
    }

    @Test
    void checkFutureIsCalledWhenSendingEmailToKafka()
            throws ServiceException, ExecutionException, InterruptedException {
        when(producer.sendAndReturnFuture(any())).thenReturn(future);
        kafkaEmailClient = new KafkaEmailClient(producer,
                avroSerializer, testSchema);
        kafkaEmailClient.sendEmailToKafka(REQUEST_ID, emailContent);
        verify(future, times(1)).get();
    }

    @Test
    void checkServiceExceptionIsThrownWhenSerializerThrowsIOException()
            throws IOException {
        doThrow(IOException.class).when(faultyAvroSerializer).serialize(emailContent, testSchema);
        kafkaEmailClient = new KafkaEmailClient(producer,
                faultyAvroSerializer, testSchema);
        assertThrows(ServiceException.class, () -> kafkaEmailClient.sendEmailToKafka(REQUEST_ID, emailContent));
    }

    @Test
    void checkServiceExceptionIsThrownWhenFutureThrowsExecutionException()
            throws ExecutionException, InterruptedException {
        when(producer.sendAndReturnFuture(any())).thenReturn(future);
        kafkaEmailClient = new KafkaEmailClient(producer,
                avroSerializer, testSchema);
        doThrow(ExecutionException.class).when(future).get();
        assertThrows(ServiceException.class, () -> kafkaEmailClient.sendEmailToKafka(REQUEST_ID, emailContent));
    }

    @Test
    void checkServiceExceptionIsThrownWhenFutureThrowsInterruptedException()
            throws ExecutionException, InterruptedException {
        when(producer.sendAndReturnFuture(any())).thenReturn(future);
        kafkaEmailClient = new KafkaEmailClient(producer,
                avroSerializer, testSchema);
        doThrow(InterruptedException.class).when(future).get();
        assertThrows(ServiceException.class, () -> kafkaEmailClient.sendEmailToKafka(REQUEST_ID, emailContent));
    }
}

