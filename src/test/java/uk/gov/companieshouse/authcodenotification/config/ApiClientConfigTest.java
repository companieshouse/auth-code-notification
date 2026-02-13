package uk.gov.companieshouse.authcodenotification.config;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.util.function.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.ApiClient;
import uk.gov.companieshouse.api.InternalApiClient;

@ExtendWith(MockitoExtension.class)
class ApiClientConfigTest {

    ApiClientConfig underTest;

    @BeforeEach
    void setUp() {
        underTest = new ApiClientConfig();
    }

    @Test
    void testApiClientSupplier() {
        Supplier<ApiClient> supplier = underTest.getApiClient();
        assertThat(supplier, is(notNullValue()));
    }

    @Test
    void getInternalApiClientSupplier() {
        Supplier<InternalApiClient> supplier = underTest.getInternalApiClient();
        assertThat(supplier, is(notNullValue()));
    }

    @Test
    void getKafkaApiClientSupplier() {
        Supplier<InternalApiClient> supplier = underTest.getKafkaApiClient("http://chs-kafka-api:8080", "test-chs-api-key");
        assertThat(supplier, is(notNullValue()));
    }
}
