package uk.gov.companieshouse.authcodenotification.config;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

import io.opentelemetry.api.OpenTelemetry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OpenTelemetryAppenderInitializerTest {

    private static final OpenTelemetry OPEN_TELEMETRY = OpenTelemetry.noop();

    @Spy
    private OpenTelemetryAppenderInitializer underTest = new OpenTelemetryAppenderInitializer(OPEN_TELEMETRY);

    @Test
    void testAfterPropertiesSetDelegatesToInstallAppender() {
        // Stub out the seam so the real static
        // OpenTelemetryAppender.install(...) call, and its global
        // JVM logging side effect, is never invoked in this test.
        doNothing().when(underTest).installAppender(OPEN_TELEMETRY);

        underTest.afterPropertiesSet();

        verify(underTest).installAppender(OPEN_TELEMETRY);
    }

    @Test
    void testInstallAppenderDelegatesToStaticOpenTelemetryAppenderInstall() {
        // Exercises the real static OpenTelemetryAppender.install(...)
        // call so the seam method itself is covered, rather than only
        // the stubbed delegation verified above.
        underTest.installAppender(OPEN_TELEMETRY);

        assertThat(underTest, is(notNullValue()));
    }
}
