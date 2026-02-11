package mywild.wildguide.framework;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.SmartLifecycle;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ApplicationLoader implements ApplicationRunner, SmartLifecycle {

    @Value("${mywild.app.dev-mode}")
    private boolean devMode;

    @Value("${server.port:8080}")
    private int port;

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    @Value("${spring.h2.console.enabled}")
    private boolean h2ConsoleEnabled;

    @Value("${spring.h2.console.path}")
    private String h2Console;

    private boolean isRunning = false;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (isRunning) {
            log.info("------>>> READY <<<------");
            if (devMode) {
                log.info("http://localhost:{}{}", port, contextPath);
                log.info("http://localhost:{}{}/swagger-ui/index.html", port, contextPath);
                if (h2ConsoleEnabled) {
                    log.info("http://localhost:{}{}", port, h2Console);
                }
            }
        }
    }

    @Override
    public void start() {
        log.info(">>> Starting the application >>>");
        isRunning = true;
    }

    @Override
    public void stop() {
        log.info("<<< Stopping the application <<<");
        isRunning = false;
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public void stop(@NonNull Runnable callback) {
        stop();
        callback.run();
    }

}
