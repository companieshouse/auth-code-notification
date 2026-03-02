package uk.gov.companieshouse.authcodenotification;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Objects;
import org.apache.avro.Schema;
import uk.gov.companieshouse.authcodenotification.email.EmailContent;
import uk.gov.companieshouse.authcodenotification.email.EmailData;

/**
 * A home for reusable 'helper' methods for our unit tests
 */
public class TestUtils {
    public static EmailContent buildEmailContent(String appId, String messageId, String messageType,
                                                 EmailData data, String recipient,
                                                 LocalDateTime createdAt) {
        return new EmailContent.Builder()
                .withOriginatingAppId(appId)
                .withMessageId(messageId)
                .withMessageType(messageType)
                .withData(data)
                .withEmailAddress(recipient)
                .withCreatedAt(createdAt)
                .build();
    }

    public static EmailData getDummyEmailData() {
        var to = "example@test.co.uk";
        var subject = "Test email submitted";
        var authCode = "OU812";
        var companyName = "TEST COMPANY";
        var companyNumber = "00001111";
        return new EmailData(subject, to, authCode, companyName, companyNumber);
    }

    public static Schema getDummySchema(URL url) throws IOException {
        String avroSchemaPath = Objects.requireNonNull(url).getFile();
        Schema.Parser parser = new Schema.Parser();
        return parser.parse(new File(avroSchemaPath));
    }

}
