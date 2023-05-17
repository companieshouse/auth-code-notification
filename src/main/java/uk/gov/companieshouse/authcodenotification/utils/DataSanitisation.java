package uk.gov.companieshouse.authcodenotification.utils;

import org.owasp.encoder.Encode;
import org.springframework.stereotype.Component;

import static uk.gov.companieshouse.authcodenotification.utils.Constants.TRUNCATED_DATA_LENGTH;

@Component
public class DataSanitisation {

    public String makeStringSafe(String input) {
        String sanitisedInput = Encode.forJava(input);
        if (sanitisedInput.length() > TRUNCATED_DATA_LENGTH) {
            sanitisedInput = sanitisedInput.substring(0, TRUNCATED_DATA_LENGTH);
        }
        return sanitisedInput;
    }
}
