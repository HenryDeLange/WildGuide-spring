package mywild.wildguide.framework.security;

import static org.springframework.security.oauth2.core.OAuth2TokenIntrospectionClaimNames.AUD;
import static org.springframework.security.oauth2.core.OAuth2TokenIntrospectionClaimNames.SUB;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtIssuerValidator;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
// @EnableWebSecurity(debug = true)
public class SecurityConfig {

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

    @Autowired
    private RSAPublicKey publicKey;

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

    /**
     * Build and configure the JwtDecoder that will be used when we receive a JWT Token. Here we take in a
     * {@see RSAPublicKey} but you can also supply a JWK uri, or a {@see SecretKey}.
     * 
     * By default, the decoder will always verify the signature with the given key 
     * and validate the timestamp to check if the JWT is still valid.
     * 
     * Per default a Public key will set the algorithm to RS256. If you want something different you can set this explicitly.
     */
    @Bean
    JwtDecoder jwtDecoder() {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withPublicKey(publicKey).build();
        decoder.setJwtValidator(tokenValidator());
        return decoder;
    }

    /**
     * We can write custom validators to validate different parts of the JWT. Per default, the framework will always
     * validate the timestamp, but we can add validators to enhance security. For instance you should always
     * validate the issuer to make sure that the JWT was issued from a known source. Remember that if we customise the
     * validation we need to re-add the timestamp validator.
     *
     * Here we crate a list of validators. The {@see JwtTimestampValidator} and the {@see JwtIssuerValidator} are
     * from the spring security framework, but we have also added a custom one. Remember if you add a custom list, you
     * must always remember to add timestamp validation or else this will be removed.
     *
     * We then place these in a {@see DelegatingOAuth2TokenValidator} that we can set to our {@see JwtDecoder}.
     */
    private OAuth2TokenValidator<Jwt> tokenValidator() {
        final List<OAuth2TokenValidator<Jwt>> validators =
                List.of(
                    new JwtTimestampValidator(),
                    new JwtIssuerValidator(issuer),
                    audienceValidator(),
                    subjectValidator()
                );
        return new DelegatingOAuth2TokenValidator<>(validators);
    }

    /**
     * You can write a custom validation by adding a {@see JwtClaimValidator} for instance below we add a custom
     * validator to the aud (audience) claim. And check that it contains a certain string.
     * {@see OAuth2TokenIntrospectionClaimNames} contains static string names of several default claims. Below we are
     * referencing the {@see OAuth2TokenIntrospectionClaimNames#AUD} string.
     */
    private OAuth2TokenValidator<Jwt> audienceValidator() {
        return new JwtClaimValidator<>(AUD, aud ->
            switch (aud) {
                case String audString -> audString.contains(audience);
                case List<?> audList -> audList.contains(audience);
                default -> false;
            }
        );
    }

    /**
     * Similar to the audience validator above, this validator checks the subject field of the JWT Token.
     */
    private OAuth2TokenValidator<Jwt> subjectValidator() {
        return new JwtClaimValidator<>(SUB, sub ->
            switch (sub) {
                case String subString -> subString.contains(subject);
                case List<?> subList -> subList.contains(subject);
                default -> false;
            }
        );
    }

    /**
     * Password encoder for the user passwords.
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
