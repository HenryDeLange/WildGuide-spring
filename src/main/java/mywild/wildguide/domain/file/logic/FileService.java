package mywild.wildguide.domain.file.logic;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import mywild.wildguide.domain.entry.data.EntryRepository;
import mywild.wildguide.domain.file.data.FileCategory;
import mywild.wildguide.domain.utils.DomainService;
import mywild.wildguide.framework.error.ApplicationException;
import mywild.wildguide.framework.error.BadRequestException;
import mywild.wildguide.framework.error.ForbiddenException;
import mywild.wildguide.framework.error.NotFoundException;
import mywild.wildguide.user.data.UserRepository;

@Slf4j
@Validated
@Service
public class FileService extends DomainService {

    @Autowired
    private UserRepository repoUser;

    @Autowired
    private EntryRepository repoEntry;

    @Value("${mywild.wildguide.file-upload-path}")
    private String fileUploadFolder;

    private Path fileUploadPath;

    @PostConstruct
    public void init() throws IOException {
        fileUploadPath = Paths.get(fileUploadFolder).normalize().toAbsolutePath();
        log.debug("Initialising file upload path: {}", fileUploadPath);
        if (!Files.exists(fileUploadPath)) {
            Files.createDirectories(fileUploadPath);
        }
    }

    public List<String> findFiles(long userId, FileCategory fileCategory, long fileCategoryId) {
        checkVisibility(userId, fileCategory, fileCategoryId);
        Path basePath = getBasePath(fileCategory, fileCategoryId);
        try (Stream<Path> stream = Files.walk(basePath)) {
            return stream
                .filter(Files::isRegularFile)
                .map(file -> getFileUrl(fileCategory, fileCategoryId, file.getFileName().toString()))
                .collect(Collectors.toList());
        }
        catch (NoSuchFileException ex) {
            log.trace(ex.getMessage(), ex);
            return Collections.emptyList();
        }
        catch (IOException ex) {
            throw new ApplicationException("file.error");
        }
    }

    public Resource findFile(long userId, FileCategory fileCategory, long fileCategoryId, String filename) throws MalformedURLException {
        checkVisibility(userId, fileCategory, fileCategoryId);
        Path filePath = getFilePath(fileCategory, fileCategoryId, filename);
        Resource resource = new UrlResource(filePath.toUri());
        if (!resource.exists() || !resource.isReadable()) {
            throw new NotFoundException("file.not-found");
        }
        return resource;
    }

    public String createFile(long userId, FileCategory fileCategory, long fileCategoryId, MultipartFile file) {
        checkVisibility(userId, fileCategory, fileCategoryId);
        String filename = "[" + UUID.randomUUID().toString() + "] " + file.getOriginalFilename();
        Path filePath = getFilePath(fileCategory, fileCategoryId, filename);
        try {
            if (!Files.exists(filePath.getParent())) {
                Files.createDirectories(filePath.getParent());
            }
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        }
        catch (IOException ex) {
            throw new ApplicationException("file.error");
        }
        return getFileUrl(fileCategory, fileCategoryId, filename);
    }

    public void deleteFile(long userId, FileCategory fileCategory, long fileCategoryId, String filename) {
        checkVisibility(userId, fileCategory, fileCategoryId);
        Path filePath = getFilePath(fileCategory, fileCategoryId, filename);
        if (!Files.exists(filePath)) {
            throw new NotFoundException("file.not-found");
        }
        try {
            Files.delete(filePath);
        }
        catch (IOException ex) {
            throw new ApplicationException("file.error");
        }
    }

    private String getFileUrl(FileCategory fileCategory, long fileCategoryId, String filename) {
        return "./" + fileCategory + "/" + fileCategoryId + "/" + filename;
    }

    private Path getBasePath(FileCategory fileCategory, long fileCategoryId) {
        Path basePath = fileUploadPath
            .resolve(fileCategory.name())
            .resolve(String.valueOf(fileCategoryId));
        return basePath;
    }

    private Path getFilePath(FileCategory fileCategory, long fileCategoryId, String filename) {
        return getBasePath(fileCategory, fileCategoryId)
            .resolve(filename)
            .normalize().toAbsolutePath();
    }

    private void checkVisibility(long userId, FileCategory fileCategory, long fileCategoryId) {
        try {
            switch (fileCategory) {
                case GUIDE:
                    checkUserHasGuideAccess(userId, fileCategoryId);                    
                    break;
                case ENTRY:
                    var entryEntity = repoEntry.findById(fileCategoryId);
                    if (!entryEntity.isPresent()) {
                        throw new BadRequestException("file.not-found");
                    }
                    var guideId = entryEntity.get().getGuideId();
                    checkUserHasGuideAccess(userId, guideId);                    
                    break;
                case USER:
                    if (!repoUser.existsById(fileCategoryId)) {
                        throw new BadRequestException("file.not-found");
                    }
                    break;
                default:
                    throw new BadRequestException("file.not-accessible");
            }
        }
        catch (ForbiddenException ex) {
            log.trace(ex.getMessage(), ex);
            throw new ForbiddenException("file.not-accessible");
        }
    }

}
