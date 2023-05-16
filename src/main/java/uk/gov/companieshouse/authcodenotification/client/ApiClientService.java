package uk.gov.companieshouse.authcodenotification.client;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.ApiClient;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.sdk.manager.ApiSdkManager;

import java.io.IOException;


@Component
public class ApiClientService {

    public InternalApiClient getInternalApiClient() {
        return ApiSdkManager.getPrivateSDK();
    }

    public ApiClient getApiClient() throws IOException {
        return ApiSdkManager.getSDK();
    }
}
