package uk.gov.companieshouse.authcodenotification.config;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.avro.Schema;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.authcodenotification.email.KafkaRestClient;
import uk.gov.companieshouse.kafka.producer.Acks;
import uk.gov.companieshouse.kafka.producer.CHKafkaProducer;
import uk.gov.companieshouse.kafka.producer.ProducerConfig;

@ExtendWith(MockitoExtension.class)
class KafkaConfigurationTest {

    private static final String SCHEMA_REGISTRY_URL = "http://chs-kafka-schemas";
    private static final String EMAIL_SCHEMA_URI = "/subjects/email-send/versions/latest";
    private static final String MAXIMUM_RETRY_ATTEMPTS = "3";

    KafkaConfiguration underTest;

    @BeforeEach
    void setUp() {
        underTest = new KafkaConfiguration(SCHEMA_REGISTRY_URL, EMAIL_SCHEMA_URI, MAXIMUM_RETRY_ATTEMPTS);
    }

    @Test
    void testFetchSchemas() throws JSONException {
        KafkaRestClient client = mock(KafkaRestClient.class);
        when(client.getSchema(SCHEMA_REGISTRY_URL, EMAIL_SCHEMA_URI)).thenReturn((
            "{ \"schema\": \"{\\\"type\\\": \\\"record\\\", \\\"name\\\": \\\"EmailContent\\\", "
                + "\\\"namespace\\\": \\\"uk.gov.companieshouse.authcodenotification.email\\\", "
                + "\\\"fields\\\": ["
                + "{\\\"name\\\": \\\"toAddress\\\", \\\"type\\\": \\\"string\\\"},"
                + "{\\\"name\\\": \\\"subject\\\", \\\"type\\\": \\\"string\\\"},"
                + "{\\\"name\\\": \\\"body\\\", \\\"type\\\": \\\"string\\\"},"
                + "{\\\"name\\\": \\\"createdAt\\\", \\\"type\\\": {\\\"type\\\": \\\"long\\\", "
                + "\\\"logicalType\\\": \\\"timestamp-millis\\\"}}"
                + "] }\" }"
        ).getBytes());

        Schema schema = underTest.fetchSchema(client);
        assertThat(schema, is(notNullValue()));

        assertThat(schema.getName(), is("EmailContent"));
    }

    @Test
    void testBuildKafkaConfig() {
        ProducerConfig config = underTest.buildKafkaConfig("kafka:9092");
        assertThat(config, is(notNullValue()));
    }

    @Disabled("Test cannot run without a running Kafka instance")
    @Test
    void testBuildKafkaProducer() {
        var config = new ProducerConfig();
        config.setRoundRobinPartitioner(true);
        config.setAcks(Acks.WAIT_FOR_ALL);
        config.setRetries(1);
        config.setEnableIdempotence(false);
        config.setBrokerAddresses(new String[]{"kafka:9092"});

        CHKafkaProducer producer = underTest.buildKafkaProducer(config);
        assertThat(producer, is(notNullValue()));
    }
}
