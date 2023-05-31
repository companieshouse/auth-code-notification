package uk.gov.companieshouse.authcodenotification.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uk.gov.companieshouse.api.interceptor.InternalUserInterceptor;

@Configuration
@ComponentScan("uk.gov.companieshouse.api.interceptor")
public class InterceptorConfig implements WebMvcConfigurer {

    private static final String ALL_PATHS = "/**";
    private static final String HEALTH_CHECK_PATH = "/**/healthcheck";

    @Autowired
    private InternalUserInterceptor internalUserInterceptor;

    /**
     * Set up the interceptors to run against endpoints when the endpoints are called
     * Interceptors are executed in the order they are added to the registry
     * @param registry The spring interceptor registry
     */
    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(internalUserInterceptor)
                .addPathPatterns(ALL_PATHS)
                .excludePathPatterns(HEALTH_CHECK_PATH);
    }
}
