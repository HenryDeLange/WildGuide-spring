package mywild.wildguide.framework.security.header;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class WebConfigurer implements WebMvcConfigurer {

    @Value("${mywild.api-path}")
    private String apiPath;

    @Autowired
    private HeaderInterceptor interceptor;

    /** 
     * Add the header interceptor.
     */
    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(interceptor)
                .addPathPatterns("/" + apiPath + "/**")
                .excludePathPatterns(
                    "/v3/api-docs", 
                    "/v3/api-docs.*", 
                    "/v3/api-docs/**", 
                    "/swagger-ui/**"
                );
    }
    
}
