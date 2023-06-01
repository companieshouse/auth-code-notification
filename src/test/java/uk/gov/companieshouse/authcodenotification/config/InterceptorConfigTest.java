package uk.gov.companieshouse.authcodenotification.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import uk.gov.companieshouse.api.interceptor.InternalUserInterceptor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InterceptorConfigTest {

    private static final String ALL_PATHS = "/**";
    private static final String MANAGEMENT_BASE_PATH = "/auth-code-notification/";
    private static final String HEALTH_CHECK_PATH_SUFFIX = "healthcheck";
    private static final String HEALTH_CHECK_PATH = MANAGEMENT_BASE_PATH + HEALTH_CHECK_PATH_SUFFIX;

    @Mock
    private InterceptorRegistry interceptorRegistry;

    @Mock
    private InterceptorRegistration interceptorRegistration;

    @Mock
    private InternalUserInterceptor internalUserInterceptor;

    @InjectMocks
    private InterceptorConfig interceptorConfig;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(interceptorConfig, "managementBasePath", MANAGEMENT_BASE_PATH);
        ReflectionTestUtils.setField(interceptorConfig, "healthCheckPathSuffix", HEALTH_CHECK_PATH_SUFFIX);
    }

    @Test
    void addInterceptorsTest() {
        when(interceptorRegistry.addInterceptor(any())).thenReturn(interceptorRegistration);
        when(interceptorRegistration.addPathPatterns(any(String.class))).thenReturn(interceptorRegistration);
        when(interceptorRegistration.excludePathPatterns(any(String.class))).thenReturn(interceptorRegistration);

        interceptorConfig.addInterceptors(interceptorRegistry);

        //  The order that the interceptors are added is important, they get executed in the order they are added,
        //  i.e. first interceptor added gets run first.
        //  This test will ensure that the interceptors are added in the correct order in InterceptorConfig

        InOrder inOrder = inOrder(interceptorRegistry, interceptorRegistration);

        // Internal User authentication interceptor check
        inOrder.verify(interceptorRegistry).addInterceptor(internalUserInterceptor);
        inOrder.verify(interceptorRegistration).addPathPatterns(ALL_PATHS);
        inOrder.verify(interceptorRegistration).excludePathPatterns(HEALTH_CHECK_PATH);
    }
}
