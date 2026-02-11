package mywild.wildguide.framework.security.header;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class HeaderInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(@NonNull HttpServletRequest req, @NonNull HttpServletResponse rsp, @NonNull Object handler) throws Exception {
        String acceptLanguage = req.getHeader("Accept-Language");
        String encoding = req.getHeader("Accept-Encoding");
        String bearerAuth = req.getHeader("bearerAuth");
        log.trace("Request URL    : {}", req.getRequestURI());
        log.trace("Request Header : Accept-Language = {}", acceptLanguage);
        log.trace("Request Header : Accept-Encoding = {}", encoding);
        log.trace("Request Header : bearerAuth      = {}", bearerAuth);
        return true;
    }

}

