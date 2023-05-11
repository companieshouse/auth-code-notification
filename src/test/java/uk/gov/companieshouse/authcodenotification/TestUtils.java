package uk.gov.companieshouse.authcodenotification;

import org.apache.avro.Schema;
import uk.gov.companieshouse.authcodenotification.email.EmailContent;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A home for reusable 'helper' methods for our unit tests
 */
public class TestUtils {
    public static EmailContent buildEmailContent(String appId, String messageId, String messageType,
                                                 Map<String, Object> data, String recipient,
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

    public static Map<String, Object> getDummyEmailData() {
        Map<String, Object> data = new HashMap<>();
        data.put("to", "example@test.co.uk");
        data.put("subject", "Test objection submitted");
        data.put("full_name", "Joe Bloggs");
        data.put("share_identity", false);
        data.put("company_name", "TEST COMPANY");
        data.put("company_number", "00001111");
        data.put("reason", "Testing this");
        return data;
    }

    public static Schema getDummySchema(URL url) throws IOException {
        String avroSchemaPath = Objects.requireNonNull(url).getFile();
        Schema.Parser parser = new Schema.Parser();
        return parser.parse(new File(avroSchemaPath));
    }

}
