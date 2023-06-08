package uk.gov.companieshouse.authcodenotification.utils;

import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.companieshouse.authcodenotification.exception.ServiceException;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EncrypterTest {

    private static final String PLAIN_TEXT_INPUT = "hello";
    private static final String DUMMY_ENCRYPTION_KEY = "jhtefgOBoV+YIjhgrfEvae9Up476543j";
    private static final String REQUEST_ID = "12345";
    private static final Map<String, Object> LOG_MAP = new HashMap<>();

    private final Encrypter encrypter = new Encrypter();

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(encrypter, "aesKeyString", DUMMY_ENCRYPTION_KEY);
    }

    @Test
    void testEncryptEncryptsStringSuccessfully() throws Exception {
        String encrypted = encrypter.encrypt(REQUEST_ID, PLAIN_TEXT_INPUT, LOG_MAP);
        assertEquals(44, encrypted.length());
        assertTrue(StringUtils.isNotBlank(encrypted));
    }

    @Test
    void testEncryptIdenticalInputProducesDifferentEncryptedString() throws Exception {
        String encrypted1 = encrypter.encrypt(REQUEST_ID, PLAIN_TEXT_INPUT, LOG_MAP);
        String encrypted2 = encrypter.encrypt(REQUEST_ID, PLAIN_TEXT_INPUT, LOG_MAP);
        assertNotEquals(encrypted1, encrypted2);
    }

    @Test
    void testEncryptThrowsServiceException() {
        assertThrows(ServiceException.class, () -> encrypter.encrypt(REQUEST_ID, null, LOG_MAP));
    }

}
