package uk.gov.companieshouse.authcodenotification.config;

import static java.lang.String.format;

import java.util.function.Supplier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import uk.gov.companieshouse.api.ApiClient;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.http.ApiKeyHttpClient;
import uk.gov.companieshouse.authcodenotification.utils.ApiLogger;
import uk.gov.companieshouse.sdk.manager.ApiSdkManager;

@Configuration
public class ApiClientConfig {

    @Primary
    @Bean(name = "internalApiClient")
    public Supplier<InternalApiClient> getInternalApiClient() {
        ApiLogger.info("getInternalApiClient() method called.");

        return ApiSdkManager::getPrivateSDK;
    }

    @Bean(name = "apiClient")
    public Supplier<ApiClient> getApiClient() {
        ApiLogger.info("getApiClient() method called.");

        return ApiSdkManager::getSDK;
    }

    @Bean(name = "chsKafkaApiClient")
    public Supplier<InternalApiClient> getKafkaApiClient(@Value("${application.chs-kafka-api.url}") String chsKafkaApiUrl,
            @Value("${application.chs-kafka-api.key}") String chsKafkaApiKey) {
        ApiLogger.info(format("getKafkaApiClient(url=%s) method called.", chsKafkaApiUrl));

        InternalApiClient internalApiClient = new InternalApiClient(new ApiKeyHttpClient(chsKafkaApiKey));
        internalApiClient.setBasePath(chsKafkaApiUrl);

        return () -> internalApiClient;
    }

}
