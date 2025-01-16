package mywild.wildguide.framework.security.limiter;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.Getter;
import mywild.wildguide.framework.security.jwt.TokenConstants;

@Component
public class UserRateLimiter {

    @Getter(AccessLevel.NONE)
    @Value("${mywild.wildguide.user-rate-limit}")
    private int userRateLimit;

    @Getter(AccessLevel.NONE)
    @Value("${mywild.wildguide.concurrent-users-limit}")
    private int concurrentUsersLimit;

    private final Cache<String, Bucket> bucketsCache = Caffeine.newBuilder()
        .expireAfterAccess(5, TimeUnit.MINUTES)
        .build();

    public Bucket getBucket(HttpServletRequest request) {
        return bucketsCache.get(getUserId(request), _ -> createNewBucket());
    }

    public boolean withinConcurrentUserLimit() {
        bucketsCache.cleanUp();
        return bucketsCache.estimatedSize() <= concurrentUsersLimit;
    }

    private Bucket createNewBucket() {
        Bandwidth limit = Bandwidth.builder()
            .capacity(userRateLimit)
            .refillGreedy(userRateLimit, Duration.ofMinutes(1))
            .build();
        return Bucket.builder()
            .addLimit(limit)
            .build();
    }

    private String getUserId(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof Jwt jwt) {
                return jwt.getClaimAsString(TokenConstants.JWT_USER_ID);
            }
            else {
                return request.getRemoteAddr();
            }
        }
        return request.getRemoteAddr();
    }

}
