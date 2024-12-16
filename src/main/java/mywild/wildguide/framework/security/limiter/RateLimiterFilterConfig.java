package mywild.wildguide.framework.security.limiter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RateLimiterFilterConfig {

    @Autowired
    private RateLimiterFilter rateLimiterFilter;

    @Bean
    FilterRegistrationBean<RateLimiterFilter> rateLimiterFilterRegistrationBean() {
        FilterRegistrationBean<RateLimiterFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(rateLimiterFilter);
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }

}
