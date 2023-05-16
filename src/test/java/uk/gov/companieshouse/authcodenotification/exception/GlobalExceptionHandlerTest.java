package uk.gov.companieshouse.authcodenotification.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;
import uk.gov.companieshouse.authcodenotification.utils.ApiLogger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static uk.gov.companieshouse.authcodenotification.utils.Constants.ERIC_REQUEST_ID_KEY;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private static final String REQUEST_ID = "1234";
    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        this.globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void testHandleExceptionReturnsCorrectResponse() {
        when(webRequest.getHeader(ERIC_REQUEST_ID_KEY)).thenReturn(REQUEST_ID);

        ResponseEntity<Object> entity = globalExceptionHandler.handleException(new Exception(), webRequest);

        assertNotNull(entity);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, entity.getStatusCode());
    }

    @Test
    void testHandleExceptionLogsException() {
        Throwable rootCause = new Throwable("root cause");
        Exception exception = new Exception("exception message", rootCause);

        when(webRequest.getHeader(ERIC_REQUEST_ID_KEY)).thenReturn(REQUEST_ID);

        try (MockedStatic<ApiLogger> apiLogger = mockStatic(ApiLogger.class)) {

            globalExceptionHandler.handleException(exception, webRequest);

            apiLogger.verify(() -> ApiLogger.errorContext(
                    eq(REQUEST_ID),
                    eq("Unhandled exception"),
                    eq(exception)), times(1));
        }
    }
}
