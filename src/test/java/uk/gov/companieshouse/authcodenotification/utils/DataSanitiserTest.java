package uk.gov.companieshouse.authcodenotification.utils;

import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DataSanitiserTest {

    @Test
    void testMakeStringSafeForLogging() {
        DataSanitiser dataSanitiser = new DataSanitiser();
        String sanitisedInput = dataSanitiser.makeStringSafe("abc\t\nabc");
        assertEquals("abc\\t\\nabc", sanitisedInput);
    }

    @Test
    void testMakeStringSafeForLoggingWithTruncation() {
        DataSanitiser dataSanitiser = new DataSanitiser();
        String sanitisedInput = dataSanitiser.makeStringSafe("abc\t\nabc" + StringUtils.repeat("A", Constants.TRUNCATED_DATA_LENGTH));
        String sanitised = "abc\\t\\nabc";
        String expected = sanitised + (StringUtils.repeat("A", Constants.TRUNCATED_DATA_LENGTH - sanitised.length()));
        assertEquals(expected, sanitisedInput);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = { "", "  " } )
    void testSanitiserBlankString(String input) {
        DataSanitiser dataSanitiser = new DataSanitiser();
        String sanitisedInput = dataSanitiser.makeStringSafe(input);
        String expected = (input == null) ? "null" : input;
        assertEquals(expected, sanitisedInput);
    }
}
