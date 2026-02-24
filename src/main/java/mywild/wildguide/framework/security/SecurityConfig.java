package mywild.wildguide.framework.security;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
// @EnableWebSecurity(debug = true)
public class SecurityConfig implements WebMvcConfigurer {

    @Value("${mywild.app.dev-mode}")
    private boolean devMode;

    @Value("${spring.h2.console.path}")
    private String h2Console;

    @Value("${mywild.jwt.issuer}")
    private String issuer;

    @Value("${mywild.jwt.subject}")
    private String subject;

    @Value("${mywild.jwt.audience}")
    private String audience;

    @Value("${mywild.cors}")
    private String cors;

    @Value("${mywild.api-path}")
    private String apiPath;

    /**
     * Set up a security filter chain and configure the access levels for the different endpoints.
     * We then configure our application into a Resource Server that accepts the JWT Token.
     */
    @Bean
    SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
            .formLogin(AbstractHttpConfigurer::disable)
            .logout(logout -> logout.disable())
            // Authorization
            // TODO: Can some/much/all of this be done on the controllers via annotations?
            .authorizeHttpRequests(authorize -> {
                authorize
                    // Root UI
                    .requestMatchers("/").permitAll()
                    .requestMatchers("/index.html").permitAll();
                if (devMode) {
                    // H2
                    authorize.requestMatchers(h2Console + "/**").permitAll();
                }
                authorize
                    // Actuator
                    // .requestMatchers("/actuator/**").permitAll()
                    // Swagger UI
                    .requestMatchers("/swagger-ui.html").permitAll()
                    .requestMatchers("/swagger-ui/**").permitAll()
                    .requestMatchers("/v3/**").permitAll()
                    // Auth Endpoints
                    .requestMatchers(apiPath + "/users/register/**").permitAll()
                    .requestMatchers(apiPath + "/users/login").permitAll()
                    .requestMatchers(apiPath + "/users/refresh/**").hasAuthority("SCOPE_refresh")
                    // Version Endpoint
                    .requestMatchers(apiPath + "/version/**").permitAll()
                    // Domain Endpoints
                    .requestMatchers(HttpMethod.GET, apiPath + "/guides/stars").authenticated()
                    .requestMatchers(HttpMethod.GET, apiPath + "/guides/**").permitAll()
                    .requestMatchers(HttpMethod.GET, apiPath + "/files/**").permitAll()
                    .requestMatchers(HttpMethod.GET, apiPath + "/icons/**").permitAll()
                    .anyRequest().hasAuthority("SCOPE_access"); // .anyRequest().authenticated()
            })
            // Indicate this is a Resource Server that accepts JWT tokens
            .oauth2ResourceServer(configure -> configure.jwt(Customizer.withDefaults()))
            // CORS
            .cors(Customizer.withDefaults())
            // CSRF
            .csrf(csrf -> csrf.disable());
            // TODO: Proper CSRF tokens handling.
            //       See https://stackoverflow.com/questions/51026694/spring-security-blocks-post-requests-despite-securityconfig
            //       .addFilterAfter(new CsrfTokenResponseHeaderBindingFilter(), CsrfFilter.class)
        if (devMode) {
            httpSecurity
                // H2
                .headers(headers -> headers.frameOptions(FrameOptionsConfig::disable));
        }
        // Build
        return httpSecurity.build();
    }

    /**
     * Perform CORS checks based on the provided allowed origins.
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(cors));
        configuration.setAllowedMethods(Arrays.asList("*"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
