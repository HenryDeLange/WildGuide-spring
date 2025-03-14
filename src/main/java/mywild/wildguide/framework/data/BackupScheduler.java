package mywild.wildguide.framework.data;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class BackupScheduler {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Scheduled(initialDelay = 1, fixedRate = 60 * 12, timeUnit = TimeUnit.MINUTES)
    public void backupDatabase() {
        log.warn("Starting database backup...");
        Path backupFolder = Paths.get("backups").resolve(LocalDate.now().toString());
        // Create a database file backup
        jdbcTemplate.execute("BACKUP TO '" + backupFolder.resolve("h2.zip").toAbsolutePath().toString() + "'");
        // Create a SQL dump
        jdbcTemplate.execute("SCRIPT DROP TO '" + backupFolder.resolve("sql.zip").toAbsolutePath().toString() + "' COMPRESSION ZIP");
        log.warn("Finished database backup!");
    }

}
