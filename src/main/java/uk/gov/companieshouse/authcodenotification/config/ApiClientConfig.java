package uk.gov.companieshouse.authcodenotification.config;

import java.util.function.Supplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.companieshouse.api.ApiClient;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.authcodenotification.utils.ApiLogger;
import uk.gov.companieshouse.sdk.manager.ApiSdkManager;

@Configuration
public class ApiClientConfig {

    @Bean
    public Supplier<InternalApiClient> getInternalApiClient() {
        ApiLogger.info("getInternalApiClient() method called.");

        return ApiSdkManager::getPrivateSDK;
    }

    @Bean
    public Supplier<ApiClient> getApiClient() {
        ApiLogger.info("getApiClient() method called.");

        return ApiSdkManager::getSDK;
    }

}
