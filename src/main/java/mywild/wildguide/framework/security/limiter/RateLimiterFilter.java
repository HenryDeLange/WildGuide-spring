package mywild.wildguide.framework.security.limiter;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        boolean isWithinLimit = true;
        if (!globalRateLimiter.getBucket().tryConsume(1)) {
            isWithinLimit = false;
            denyRequest(response, "From all users.");
        }
        if (!userRateLimiter.getBucket(httpRequest).tryConsume(1) && isWithinLimit) {
            isWithinLimit = false;
            denyRequest(response, "From this user.");
        }
        if (isWithinLimit && !userRateLimiter.withinConcurrentUserLimit()) {
            isWithinLimit = false;
            denyRequest(response, "Too many active users.");
        }
        if (isWithinLimit) {
            chain.doFilter(request, response);
        }
    }

    private void denyRequest(ServletResponse response, String message) throws IOException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setStatus(429);
        httpResponse.getWriter().write("Too many requests: " + message);
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
