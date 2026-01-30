package uk.gov.companieshouse.authcodenotification.config;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.time.LocalDateTime;
import java.util.function.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;
import uk.gov.companieshouse.authcodenotification.email.KafkaRestClient;
import uk.gov.companieshouse.logging.Logger;

@ExtendWith(MockitoExtension.class)
class ApplicationConfigurationTest {

    ApplicationConfiguration underTest;

    @BeforeEach
    void setUp() {
        underTest = new ApplicationConfiguration();
    }

    @Test
    void testLogger() {
        Logger logger = underTest.logger();

        assertThat(logger, is(notNullValue()));
    }

    @Test
    void testDateTimeNow() {
        Supplier<LocalDateTime> localDateTimeSupplier = underTest.dateTimeNow();

        assertThat(localDateTimeSupplier, is(notNullValue()));
    }

    @Test
    void testKafkaRestClient() {
        RestTemplate restTemplate = new RestTemplateBuilder().build();
        KafkaRestClient kafkaRestClient = underTest.kafkaRestClient(restTemplate);

        assertThat(kafkaRestClient, is(notNullValue()));
    }

    @Test
    void testRestTemplate() {
        RestTemplate restTemplate = underTest.restTemplate(new RestTemplateBuilder());

        assertThat(restTemplate, is(notNullValue()));
    }
}
