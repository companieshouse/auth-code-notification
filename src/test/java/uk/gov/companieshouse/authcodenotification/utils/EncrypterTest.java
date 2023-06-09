package uk.gov.companieshouse.authcodenotification.utils;

import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.InvalidKeyException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EncrypterTest {

    private static final String PLAIN_TEXT_INPUT = "hello";
    private static final String DUMMY_ENCRYPTION_KEY = "jhtefgOBoV+YIjhgrfEvae9Up476543j";
    private static final String DUMMY_ENCRYPTION_KEY_TOO_LONG = "jhtefgOBoV+YIjhgrfEvae9Up476543jgfhfgh";


    private Encrypter encrypter;

    @BeforeEach
    public void setup () {
         encrypter = new Encrypter();
    }
    @Test
    void testEncryptEncryptsStringSuccessfully() throws Exception {
        String encrypted = encrypter.encrypt(PLAIN_TEXT_INPUT, DUMMY_ENCRYPTION_KEY);
        assertEquals(44, encrypted.length());
        assertTrue(StringUtils.isNotBlank(encrypted));
    }

    @Test
    void testEncryptIdenticalInputProducesDifferentEncryptedString() throws Exception {
        String encrypted1 = encrypter.encrypt(PLAIN_TEXT_INPUT, DUMMY_ENCRYPTION_KEY);
        String encrypted2 = encrypter.encrypt(PLAIN_TEXT_INPUT, DUMMY_ENCRYPTION_KEY);
        assertNotEquals(encrypted1, encrypted2);
    }

    @Test
    void testEncryptThrowsNullPointerExceptionWhenPlainTextToEncryptParamIsNull() {
        Exception e = assertThrows(NullPointerException.class, () -> encrypter.encrypt(null, DUMMY_ENCRYPTION_KEY));
        assertEquals("plainTextToEncrypt must not be null", e.getMessage());
    }

    @Test
    void testEncryptThrowsNullPointerExceptionWhenKeyParamIsNull() {
        Exception e = assertThrows(NullPointerException.class, () -> encrypter.encrypt(PLAIN_TEXT_INPUT, null));
        assertEquals("key must not be null", e.getMessage());
    }

    @Test
    void testEncryptThrowsInvalidKeyException() {
        assertThrows(InvalidKeyException.class, () -> encrypter.encrypt(PLAIN_TEXT_INPUT, DUMMY_ENCRYPTION_KEY_TOO_LONG));
    }

}
