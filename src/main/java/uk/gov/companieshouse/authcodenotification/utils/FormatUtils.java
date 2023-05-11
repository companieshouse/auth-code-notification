package uk.gov.companieshouse.authcodenotification.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FormatUtils {

    private static final String CREATED_AT_FORMAT = "dd MMM yyyy HH:mm:ss";

    private FormatUtils() {}

    public static String formatTimestamp(LocalDateTime timestamp) {
        return timestamp.format(DateTimeFormatter.ofPattern(CREATED_AT_FORMAT));
    }
}
