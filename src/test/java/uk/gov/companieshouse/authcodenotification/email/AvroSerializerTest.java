package uk.gov.companieshouse.authcodenotification.email;

import org.apache.avro.Schema;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.authcodenotification.TestUtils;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;

class AvroSerializerTest {

    private static final LocalDateTime CREATED_AT = LocalDateTime.of(2020, 1, 1, 5, 0);
    private static final String EMAIL_TEMPLATE_MESSAGE_TYPE = "test_confirmation_email";
    private static final String MESSAGE_ID = "abc";
    private static final String APP_ID = "strike-off-objections-api";
    private static final String RECIPIENT = "example@test.co.uk";
    private static final String EXPECTED_CREATED_AT = "01 Jan 2020 05:00:00";

    private final AvroSerializer avroSerializer = new AvroSerializer();

    @Test
    void testAvroSerializerForEmailContent() throws IOException {
        Schema schema = TestUtils.getDummySchema(this.getClass().getClassLoader().getResource(
                "email/email-send.avsc"));
        EmailContent emailContent = TestUtils.buildEmailContent(
                APP_ID,
                MESSAGE_ID,
                EMAIL_TEMPLATE_MESSAGE_TYPE,
                TestUtils.getDummyEmailData(),
                RECIPIENT,
                CREATED_AT);

        byte[] byteArray = avroSerializer.serialize(emailContent, schema);
        String result = new String(byteArray);

        assertTrue(result.contains(APP_ID));
        assertTrue(result.contains(MESSAGE_ID));
        assertTrue(result.contains(TestUtils.getDummyEmailData().get("to").toString()));
        assertTrue(result.contains(TestUtils.getDummyEmailData().get("subject").toString()));
        assertTrue(result.contains(TestUtils.getDummyEmailData().get("company_name").toString()));
        assertTrue(result.contains(TestUtils.getDummyEmailData().get("company_number").toString()));
        assertTrue(result.contains(TestUtils.getDummyEmailData().get("reason").toString()));
        assertTrue(result.contains(RECIPIENT));
        assertTrue(result.contains(EXPECTED_CREATED_AT));
    }
}
