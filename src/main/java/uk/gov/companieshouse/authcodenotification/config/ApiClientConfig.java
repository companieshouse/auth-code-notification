package uk.gov.companieshouse.authcodenotification.config;

import java.util.function.Supplier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.companieshouse.api.ApiClient;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.authcodenotification.utils.ApiLogger;
import uk.gov.companieshouse.sdk.manager.ApiSdkManager;

@Configuration
public class ApiClientConfig {

    @Bean
    public Supplier<InternalApiClient> getInternalApiClient(@Value("${application.oracle-query.api-url}") String oracleQueryApiUrl) {
        ApiLogger.info("getInternalApiClient(oracleQueryApiYUrl=%s) method called.".formatted(oracleQueryApiUrl));

        InternalApiClient apiClient = ApiSdkManager.getPrivateSDK();
        apiClient.setBasePath(oracleQueryApiUrl);

        return () -> apiClient;
    }

    @Bean
    public Supplier<ApiClient> getApiClient() {
        ApiLogger.info("getApiClient() method called.");

        return ApiSdkManager::getSDK;
    }

}
