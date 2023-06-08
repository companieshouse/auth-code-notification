package uk.gov.companieshouse.authcodenotification.utils;

import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EncrypterTest {

    private static final String PLAIN_TEXT_INPUT = "hello";
    private static final String DUMMY_ENCRYPTION_KEY = "jhtefgOBoV+YIjhgrfEvae9Up476543j";

    private final Encrypter encrypter = new Encrypter();

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(encrypter, "aesKeyString", DUMMY_ENCRYPTION_KEY);
    }

    @Test
    void testEncryptsStringSuccessfully() throws Exception {
        String encrypted = encrypter.encrypt(PLAIN_TEXT_INPUT);
        assertEquals(44, encrypted.length());
        assertTrue(StringUtils.isNotBlank(encrypted));
    }

    @Test
    void testIdenticalInputProducesDifferentEncryptedString() throws Exception {
        String encrypted1 = encrypter.encrypt(PLAIN_TEXT_INPUT);
        String encrypted2 = encrypter.encrypt(PLAIN_TEXT_INPUT);
        assertNotEquals(encrypted1, encrypted2);
    }

    @Test
    void testEncryptNullThrowsError() {
        assertThrows(Exception.class, () -> encrypter.encrypt(null));
    }

}
