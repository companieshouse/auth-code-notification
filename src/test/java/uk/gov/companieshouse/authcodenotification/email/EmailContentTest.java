package uk.gov.companieshouse.authcodenotification.email;

import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.authcodenotification.TestUtils;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EmailContentTest {

    private static final String ORIGINATING_APP_ID = "APP_ID";
    private static final String MESSAGE_ID = "MESSAGE_ID";
    private static final String MESSAGE_TYPE = "MESSAGE_TYPE";
    private static final String EMAIL_ADDRESS = "test@test.com";
    private static final LocalDateTime CREATED_AT =
            LocalDateTime.of(2020, 1, 1, 0, 0);
   @Test
    void emailBuilderTest() {
        EmailContent emailContent = new EmailContent.Builder()
                .withOriginatingAppId(ORIGINATING_APP_ID)
                .withEmailAddress(EMAIL_ADDRESS)
                .withCreatedAt(CREATED_AT)
                .withData(TestUtils.getDummyEmailData())
                .withMessageId(MESSAGE_ID)
                .withMessageType(MESSAGE_TYPE)
                .build();

        assertEquals(ORIGINATING_APP_ID, emailContent.getOriginatingAppId());
        assertEquals(MESSAGE_ID, emailContent.getMessageId());
        assertEquals(MESSAGE_TYPE, emailContent.getMessageType());
        assertEquals(TestUtils.getDummyEmailData(), emailContent.getData());
        assertEquals(EMAIL_ADDRESS, emailContent.getEmailAddress());
        assertEquals(CREATED_AT, emailContent.getCreatedAt());
    }
}