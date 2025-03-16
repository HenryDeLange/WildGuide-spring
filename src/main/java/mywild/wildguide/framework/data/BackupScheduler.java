package mywild.wildguide.framework.data;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class BackupScheduler {

    private Path baseBackupFolder = Paths.get("data", "backups");

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${mywild.app.backup-retention}")
    private int backupRetention;

    @Scheduled(initialDelay = 30, fixedRate = 60 * 60 * 12, timeUnit = TimeUnit.SECONDS)
    public void backupDatabase() {
        log.warn("Starting database backup");
        Path backupFolder = baseBackupFolder.resolve(LocalDate.now().toString());
        // // Create a database file backup
        // jdbcTemplate.execute("BACKUP TO '" + backupFolder.resolve("h2.zip").toAbsolutePath().toString() + "'");
        // Create a SQL dump
        jdbcTemplate.execute("SCRIPT DROP TO '" + backupFolder.resolve("sql.zip").toAbsolutePath().toString() + "' COMPRESSION ZIP");
        log.warn("Finished database backup");
    }

    @Scheduled(initialDelay = 45, fixedRate = 60 * 60 * 24, timeUnit = TimeUnit.SECONDS)
    public void cleanOldBackups() {
        log.warn("Cleaning up old database backups");
        try {
            Files.list(baseBackupFolder)
                .filter(Files::isDirectory)
                .filter(path -> {
                    try {
                        LocalDate folderDate = LocalDate.parse(path.getFileName().toString());
                        return folderDate.isBefore(LocalDate.now().minusDays(backupRetention));
                    }
                    catch (Exception ex) {
                        log.warn("Error parsing backup folder name, expected a date: {}", path);
                        return false;
                    }
                })
                .forEach(path -> {
                    try {
                        Files.walk(path).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
                        log.warn("Deleted old backup folder: {}", path);
                    }
                    catch (Exception ex) {
                        log.warn("Error deleting folder: {}", path, ex);
                    }
                });
        }
        catch (Exception e) {
            log.error("Error cleaning up old backups", e);
        }
        log.warn("Finished cleaning up old database backups");
    }

}
