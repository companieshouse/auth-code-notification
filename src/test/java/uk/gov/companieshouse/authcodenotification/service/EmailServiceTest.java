package uk.gov.companieshouse.authcodenotification.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.companieshouse.authcodenotification.email.EmailClient;
import uk.gov.companieshouse.authcodenotification.email.EmailContent;
import uk.gov.companieshouse.authcodenotification.exception.ServiceException;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    private static final String REQUEST_ID = "REQUEST_ID";
    private static final String COMPANY_NUMBER = "COMPANY_NUMBER";
    private static final String COMPANY_NAME = "Company: " + COMPANY_NUMBER;
    private static final String FORMATTED_EMAIL_SUBJECT = COMPANY_NUMBER + ": email sent";
    private static final LocalDateTime LOCAL_DATE_TIME = LocalDateTime.of(2020, 12, 10, 8, 0);
    private static final String EMAIL_ADDRESS = "demo@ch.gov.uk";

    private static final String AUTH_CODE = "jgjh34343jh3jh";

    @Mock
    private EmailClient emailClient;

    @Mock
    private Supplier<LocalDateTime> dateTimeSupplier;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(emailService, "emailSubject", FORMATTED_EMAIL_SUBJECT);
    }

    @Test
    void sendAuthCodeEmail() throws ServiceException {
        when(dateTimeSupplier.get()).thenReturn(LOCAL_DATE_TIME);

        emailService.sendAuthCodeEmail(
                REQUEST_ID,
                AUTH_CODE,
                COMPANY_NAME,
                COMPANY_NUMBER,
                EMAIL_ADDRESS
        );

        ArgumentCaptor<EmailContent> emailContentArgumentCaptor = ArgumentCaptor.forClass(EmailContent.class);
        verify(emailClient, times(1)).sendEmail(eq(REQUEST_ID), emailContentArgumentCaptor.capture());

        EmailContent emailContent = emailContentArgumentCaptor.getValue();

        assertEquals(EMAIL_ADDRESS, emailContent.getEmailAddress());
        assertEquals(LOCAL_DATE_TIME, emailContent.getCreatedAt());

        Map<String, Object> data = emailContent.getData();

        assertEquals(EMAIL_ADDRESS, data.get("to"));
        assertEquals(FORMATTED_EMAIL_SUBJECT, data.get("subject"));
        assertEquals("Company: " + COMPANY_NUMBER, data.get("company_name"));
        assertEquals(COMPANY_NUMBER, data.get("company_number"));
        assertEquals(AUTH_CODE, data.get("auth_code"));
    }

}
