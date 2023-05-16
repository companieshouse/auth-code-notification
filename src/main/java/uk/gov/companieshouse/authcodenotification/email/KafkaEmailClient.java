package uk.gov.companieshouse.authcodenotification.email;

import org.apache.avro.Schema;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.authcodenotification.exception.ServiceException;
import uk.gov.companieshouse.authcodenotification.utils.ApiLogger;
import uk.gov.companieshouse.kafka.message.Message;
import uk.gov.companieshouse.kafka.producer.CHKafkaProducer;

import java.io.IOException;
import java.time.ZoneId;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Component
public class KafkaEmailClient {

    @Value("${EMAIL_SEND_QUEUE_TOPIC}")
    private String emailSendQueueTopic;

    private final CHKafkaProducer producer;
    private final AvroSerializer avroSerializer;
    private final Schema schema;

    @Autowired
    public KafkaEmailClient(CHKafkaProducer producer,
                            AvroSerializer avroSerializer,
                            Schema schema) {
        this.producer = producer;
        this.avroSerializer = avroSerializer;
        this.schema = schema;
    }

    public void sendEmailToKafka(String requestId, EmailContent emailContent) throws ServiceException {
        final var errorMessage = "Error sending email to kafka";
        try {
            var message = new Message();
            byte[] serializedData = avroSerializer.serialize(emailContent, schema);
            message.setValue(serializedData);
            message.setTopic(emailSendQueueTopic);
            message.setTimestamp(emailContent.getCreatedAt().atZone(ZoneId.systemDefault()).toEpochSecond());
            Future<RecordMetadata> future = producer.sendAndReturnFuture(message);
            future.get();
        } catch (IOException | ExecutionException e) {
            ApiLogger.errorContext(requestId, errorMessage, e);
            throw new ServiceException(e.getMessage(), e);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            ApiLogger.errorContext(requestId, errorMessage, ie);
            throw new ServiceException("Thread interrupted when future was sent and returned", ie);
        }
    }

}
