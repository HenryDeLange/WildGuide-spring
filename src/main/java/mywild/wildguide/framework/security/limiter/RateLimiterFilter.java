package mywild.wildguide.framework.security.limiter;

import java.io.IOException;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RateLimiterFilter implements Filter {

    @Autowired
    private GlobalRateLimiter globalRateLimiter;

    @Autowired
    private UserRateLimiter userRateLimiter;

    @Autowired
    private MessageSource messageSource;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (!(request instanceof HttpServletRequest)) {
            return;
        }
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        boolean isWithinLimit = true;
        if (!globalRateLimiter.getBucket().tryConsume(1)) {
            isWithinLimit = false;
            denyRequest(response, "rate-limiter.all-users", httpRequest.getLocale());
        }
        if (!userRateLimiter.getBucket(httpRequest).tryConsume(1) && isWithinLimit) {
            isWithinLimit = false;
            denyRequest(response, "rate-limiter.current-user", httpRequest.getLocale());
        }
        if (isWithinLimit && !userRateLimiter.withinConcurrentUserLimit()) {
            isWithinLimit = false;
            denyRequest(response, "rate-limiter.active-users", httpRequest.getLocale());
        }
        if (isWithinLimit) {
            chain.doFilter(request, response);
        }
    }

    private void denyRequest(ServletResponse response, String message, Locale locale) throws IOException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setStatus(429);
        String translatedMessage = messageSource.getMessage(message, null, null, locale);
        String jsonBody = "{ \"reason\": \"" + translatedMessage + "\" }";
        httpResponse.getWriter().write(jsonBody);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization code, if necessary
    }

    @Override
    public void destroy() {
        // Cleanup code, if necessary
    }

}
