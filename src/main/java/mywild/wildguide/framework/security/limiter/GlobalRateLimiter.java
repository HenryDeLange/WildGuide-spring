package mywild.wildguide.framework.security.limiter;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.Getter;

@Getter
@Component
public class GlobalRateLimiter {

    @Getter(AccessLevel.NONE)
    @Value("${mywild.wildguide.global-rate-limit}")
    private int globalRateLimit;

    private Bucket bucket;

    @PostConstruct
    public void init() {
        Bandwidth limit = Bandwidth.builder()
            .capacity(globalRateLimit)
            .refillGreedy(globalRateLimit, Duration.ofMinutes(1))
            .build();
        bucket = Bucket.builder()
            .addLimit(limit)
            .build();
    }

}
