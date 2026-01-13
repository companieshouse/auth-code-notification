package uk.gov.companieshouse.authcodenotification.config;

import static uk.gov.companieshouse.authcodenotification.AuthCodeNotificationApplication.APPLICATION_NAME_SPACE;

import java.time.LocalDateTime;
import java.util.function.Supplier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import uk.gov.companieshouse.authcodenotification.email.KafkaRestClient;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Configuration
public class ApplicationConfiguration {

    @Bean
    public Logger logger() {
        return LoggerFactory.getLogger(APPLICATION_NAME_SPACE);
    }

    @Bean
    public Supplier<LocalDateTime> dateTimeNow() {
        return LocalDateTime::now;
    }

    @Bean
    public KafkaRestClient restClient(RestTemplate restTemplate) {
        return new KafkaRestClient(restTemplate);
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}
