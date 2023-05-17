package uk.gov.companieshouse.authcodenotification.utils;

import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DataSanitisationTest {

    @Test
    void testMakeStringSafeForLogging() {
        DataSanitisation dataSanitisation = new DataSanitisation();
        String sanitisedInput = dataSanitisation.makeStringSafe("abc\t\nabc");
        assertEquals("abc\\t\\nabc", sanitisedInput);
    }

    @Test
    void testMakeStringSafeForLoggingWithTruncation() {
        DataSanitisation dataSanitisation = new DataSanitisation();
        String sanitisedInput = dataSanitisation.makeStringSafe("abc\t\nabc" + StringUtils.repeat("A", Constants.TRUNCATED_DATA_LENGTH));
        String sanitised = "abc\\t\\nabc";
        String expected = sanitised + (StringUtils.repeat("A", Constants.TRUNCATED_DATA_LENGTH - sanitised.length()));
        assertEquals(expected, sanitisedInput);
    }
}
