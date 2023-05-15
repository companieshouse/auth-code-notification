package uk.gov.companieshouse.authcodenotification.utils;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FormatUtilsTest {

    @Test
    void testFormatTimestamp() {
        LocalDateTime timestamp = LocalDateTime.of(2020,1,1,3,10,45);
        String result = FormatUtils.formatTimestamp(timestamp);
        assertEquals("01 Jan 2020 03:10:45", result);
    }
}
