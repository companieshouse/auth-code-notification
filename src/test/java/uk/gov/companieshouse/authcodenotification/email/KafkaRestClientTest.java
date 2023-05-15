package uk.gov.companieshouse.authcodenotification.email;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaRestClientTest {

    private final String schemaRegistryUrl = "http://testSchema:1000";
    private final String emailSchemaUri = "/subjects/test-email-send";
    private ResponseEntity<byte[]> response;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private KafkaRestClient restClient;

    @BeforeEach
    void setUp() {
        String schemaUrl = String.format("%s%s", schemaRegistryUrl, emailSchemaUri);
        response = new ResponseEntity<>("abc".getBytes(), HttpStatus.OK);
        when(restTemplate.exchange(eq(schemaUrl), eq(HttpMethod.GET), any(), eq(byte[].class))).thenReturn(response);
    }

    @Test
    void testExchangeHasBeenCalled() {
        byte[] schema = restClient.getSchema(schemaRegistryUrl, emailSchemaUri);
        verify(restTemplate, times(1)).exchange(eq(schemaRegistryUrl + emailSchemaUri), eq(HttpMethod.GET), any(),
                eq(byte[].class));
        assertEquals(response.getBody(), schema);
    }
}
