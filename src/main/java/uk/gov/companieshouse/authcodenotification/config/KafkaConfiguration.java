package uk.gov.companieshouse.authcodenotification.config;

import org.apache.avro.Schema;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.companieshouse.authcodenotification.email.KafkaRestClient;
import uk.gov.companieshouse.kafka.producer.Acks;
import uk.gov.companieshouse.kafka.producer.CHKafkaProducer;
import uk.gov.companieshouse.kafka.producer.ProducerConfig;
import uk.gov.companieshouse.kafka.producer.ProducerConfigHelper;


@Configuration
public class KafkaConfiguration {

    @Value("${spring.kafka.schema.registry-url}")
    private String schemaRegistryUrl;

    @Value("${spring.kafka.email.schema-uri}")
    private String emailSchemaUri;

    @Value("${spring.kafka.producer.maximum-retry-attempts}")
    private String maximumRetryAttempts;

    @Bean
    public Schema fetchSchema(KafkaRestClient restClient) throws JSONException {
        byte[] bytes = restClient.getSchema(schemaRegistryUrl, emailSchemaUri);
        var schemaJson = new JSONObject(new String(bytes)).getString("schema");
        return new Schema.Parser().parse(schemaJson);
    }

    @Bean
    public CHKafkaProducer buildKafkaProducer(@Value("${spring.kafka.bootstrap-servers}") String bootstrapServers) {
        var config = new ProducerConfig();
        config.setRoundRobinPartitioner(true);
        config.setAcks(Acks.WAIT_FOR_ALL);
        config.setRetries(Integer.parseInt(maximumRetryAttempts));
        config.setEnableIdempotence(false);
        config.setBrokerAddresses(bootstrapServers.split(","));

        return new CHKafkaProducer(config);
    }
}
