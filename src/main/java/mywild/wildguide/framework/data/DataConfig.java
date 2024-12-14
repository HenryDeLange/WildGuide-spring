package mywild.wildguide.framework.data;

import java.sql.SQLException;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jdbc.repository.config.EnableJdbcAuditing;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import mywild.wildguide.framework.security.jwt.TokenConstants;

@Configuration
@EnableJdbcAuditing
@Slf4j
public class DataConfig {

    @Autowired
    private DataSource dataSource;

    @PreDestroy
    public void closeDataSource() {
        try {
            DataSourceUtils.releaseConnection(dataSource.getConnection(), dataSource);
        }
        catch (SQLException ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    @Bean
    AuditorAware<String> auditorProvider() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return Optional.empty();
            }
            Object principal = authentication.getPrincipal();
            if (principal instanceof Jwt jwt) {
                return Optional.of(jwt.getClaimAsString(TokenConstants.JWT_USER_ID));
            }
            else {
                return Optional.of("0");
            }
        };
    }

}
