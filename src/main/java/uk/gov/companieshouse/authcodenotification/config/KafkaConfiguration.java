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

@Configuration
public class KafkaConfiguration {

    private final String schemaRegistryUrl;
    private final String emailSchemaUri;
    private final String maximumRetryAttempts;

    public KafkaConfiguration(@Value("${spring.kafka.schema.registry-url}") String schemaRegistryUrl,
                              @Value("${spring.kafka.email.schema-uri}") String emailSchemaUri,
                              @Value("${spring.kafka.producer.maximum-retry-attempts}") String maximumRetryAttempts) {
        this.schemaRegistryUrl = schemaRegistryUrl;
        this.emailSchemaUri = emailSchemaUri;
        this.maximumRetryAttempts = maximumRetryAttempts;
    }

    @Bean
    public Schema fetchSchema(KafkaRestClient restClient) throws JSONException {
        byte[] bytes = restClient.getSchema(schemaRegistryUrl, emailSchemaUri);
        var schemaJson = new JSONObject(new String(bytes)).getString("schema");
        return new Schema.Parser().parse(schemaJson);
    }

    @Bean
    public ProducerConfig buildKafkaConfig(@Value("${spring.kafka.bootstrap-servers}") String bootstrapServers) {
        var config = new ProducerConfig();
        config.setRoundRobinPartitioner(true);
        config.setAcks(Acks.WAIT_FOR_ALL);
        config.setRetries(Integer.parseInt(maximumRetryAttempts));
        config.setEnableIdempotence(false);
        config.setBrokerAddresses(bootstrapServers.split(","));

        return config;
    }

    @Bean
    public CHKafkaProducer buildKafkaProducer(ProducerConfig producerConfig) {
        return new CHKafkaProducer(producerConfig);
    }
}
