package mywild.wildguide;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.cassandra.CassandraAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoReactiveAutoConfiguration;
import org.springframework.boot.autoconfigure.neo4j.Neo4jAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.context.SmartLifecycle;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication(
    scanBasePackageClasses = WildGuideApplication.class,
    exclude = {
        HibernateJpaAutoConfiguration.class,
        RedisAutoConfiguration.class,
        JpaRepositoriesAutoConfiguration.class,
        MongoAutoConfiguration.class,
        FlywayAutoConfiguration.class,
        JmxAutoConfiguration.class,
        MailSenderAutoConfiguration.class,
        RabbitAutoConfiguration.class,
        MongoReactiveAutoConfiguration.class,
        CassandraAutoConfiguration.class,
        Neo4jAutoConfiguration.class,
        ErrorMvcAutoConfiguration.class
    }
)
@EnableScheduling
public class WildGuideApplication {

    public static void main(String[] args) {
        SpringApplication.run(WildGuideApplication.class, args);
    }

    @Component
    public class ApplicationLoader implements ApplicationRunner, SmartLifecycle {

        @Value("${mywild.app.devMode}")
        private boolean devMode;

        @Value("${spring.h2.console.path}")
        private String h2Console;

        @Autowired
        private Environment environment;

        private boolean isRunning = false;

        @Override
        public void run(ApplicationArguments args) throws Exception {
            if (isRunning) {
                log.info("------>>> READY <<<------");
                log.info("http://localhost:{}", environment.getProperty("local.server.port"));
                if (devMode) {
                    log.info("http://localhost:{}/swagger-ui/index.html", environment.getProperty("local.server.port"));
                    log.info("http://localhost:{}{}", environment.getProperty("local.server.port"), h2Console);
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

}
